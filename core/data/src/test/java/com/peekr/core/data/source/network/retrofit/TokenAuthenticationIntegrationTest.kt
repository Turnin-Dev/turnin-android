package com.peekr.core.data.source.network.retrofit

import com.peekr.core.data.eventBus.AuthEventBus
import com.peekr.core.data.source.local.datastore.DataStoreKey
import com.peekr.core.data.source.local.datastore.DataStoreManager
import com.peekr.core.data.source.network.api.RefreshTokenApi
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.Request
import okhttp3.Response
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TokenAuthenticationIntegrationTest {
    private val dataStoreManager: DataStoreManager = mockk()
    private val refreshTokenApi: RefreshTokenApi = mockk()
    private val authEventBus: AuthEventBus = AuthEventBus()
    private lateinit var tokenAuthenticator: TokenAuthenticator

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        tokenAuthenticator = TokenAuthenticator(
            dataStoreManager,
            refreshTokenApi,
            authEventBus,
        )

        // mock 설정
        coEvery { dataStoreManager.deleteStringData(DataStoreKey.Auth.AccessToken) } just Runs
        coEvery { dataStoreManager.deleteStringData(DataStoreKey.Auth.RefreshToken) } just Runs
        coEvery { dataStoreManager.deleteLongData(DataStoreKey.User.UserId) } just Runs
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `RefreshToken이 없으면 null을 반환하고 로그아웃 이벤트 발생`() = runTest {
        // given
        val logoutEvents = mutableListOf<Unit>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            authEventBus.logoutEvent.collect {
                logoutEvents.add(it)
            }
        }
        val mockResponse = mockk<Response>()

        coEvery {
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.RefreshToken)
        } returns flowOf(null)

        // when
        val result = tokenAuthenticator.authenticate(null, mockResponse)

        // then
        assertNull(result)
        assertTrue(logoutEvents.size == 1)
        verifyDataDeletion()

        // cleanup
        job.cancel()
    }

    @Test
    fun `토큰 갱신 실패 시 null을 반환하고 로그아웃 이벤트 발생`() = runTest {
        // given
        val logoutEvents = mutableListOf<Unit>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            authEventBus.logoutEvent.collect {
                logoutEvents.add(it)
            }
        }
        val mockResponse = mockk<Response> {
            every { request } returns mockk {
                every { newBuilder() } returns mockk()
            }
        }

        coEvery {
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.RefreshToken)
        } returns flowOf("expired_refresh_token")

        val failedResponse = mockk<retrofit2.Response<TokenResponse>> {
            every { isSuccessful } returns false
            every { code() } returns 401
        }
        coEvery { refreshTokenApi.refresh(any()) } returns failedResponse

        // when
        val result = tokenAuthenticator.authenticate(null, mockResponse)

        // then
        assertNull(result)
        assertTrue(logoutEvents.size == 1)
        verifyDataDeletion()

        // cleanup
        job.cancel()
    }

    @Test
    fun `토큰 갱신 응답 바디가 null이면 로그아웃 이벤트 발생`() = runTest {
        // given
        val logoutEvents = mutableListOf<Unit>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            authEventBus.logoutEvent.collect {
                logoutEvents.add(it)
            }
        }
        val mockResponse = mockk<Response>()

        coEvery {
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.RefreshToken)
        } returns flowOf("valid_refresh_token")
        val successResponse = mockk<retrofit2.Response<TokenResponse>> {
            every { isSuccessful } returns true
            every { code() } returns 200
            every { body() } returns null
        }
        coEvery { refreshTokenApi.refresh(any()) } returns successResponse

        // when
        val result = tokenAuthenticator.authenticate(null, mockResponse)

        // then
        assertNull(result)
        assertTrue(logoutEvents.size == 1)
        verifyDataDeletion()

        // cleanup
        job.cancel()
    }

    @Test
    fun `토큰 갱신 성공 시 새 토큰으로 Request 반환하고 로그아웃 이벤트 발생 안함`() = runTest {
        // given
        val logoutEvents = mutableListOf<Unit>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            authEventBus.logoutEvent.collect {
                logoutEvents.add(it)
            }
        }
        val mockRequest = mockk<Request>()
        val mockRequestBuilder = mockk<Request.Builder> {
            every { header(any(), any()) } returns this
            every { build() } returns mockRequest
        }
        val mockResponse = mockk<Response> {
            every { request } returns mockk {
                every { newBuilder() } returns mockRequestBuilder
            }
        }
        val expectedAccessToken = "new_access_token"
        val expectedRefreshToken = "new_refresh_token"
        coEvery {
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.RefreshToken)
        } returns flowOf("valid_refresh_token")
        coEvery {
            dataStoreManager.saveEncryptedStringData(
                DataStoreKey.Auth.AccessToken,
                expectedAccessToken,
            )
        } just Runs
        coEvery {
            dataStoreManager.saveEncryptedStringData(
                DataStoreKey.Auth.RefreshToken,
                expectedRefreshToken,
            )
        } just Runs
        val newTokenResponse = TokenResponse(expectedAccessToken, expectedRefreshToken)
        val successResponse = mockk<retrofit2.Response<TokenResponse>> {
            every { isSuccessful } returns true
            every { code() } returns 200
            every { body() } returns newTokenResponse
        }
        coEvery { refreshTokenApi.refresh(any()) } returns successResponse

        // when
        val result = tokenAuthenticator.authenticate(null, mockResponse)

        // then
        assertNotNull(result)
        assertTrue(logoutEvents.isEmpty())
        coVerify {
            dataStoreManager.saveEncryptedStringData(
                DataStoreKey.Auth.AccessToken,
                expectedAccessToken,
            )
        }
        coVerify {
            dataStoreManager.saveEncryptedStringData(
                DataStoreKey.Auth.RefreshToken,
                expectedRefreshToken,
            )
        }
        verify { mockRequestBuilder.header("Authorization", "Bearer $expectedAccessToken") }

        // cleanup
        job.cancel()
    }

    // TODO: 검토 필요
    @Test
    fun `여러 번 실패해도 매번 로그아웃 이벤트가 발생한다`() = runTest {
        // given
        val logoutEvents = mutableListOf<Unit>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            authEventBus.logoutEvent.collect {
                logoutEvents.add(it)
            }
        }

        val mockResponse = mockk<Response>()
        coEvery {
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.RefreshToken)
        } returns flowOf(null)

        // when
        tokenAuthenticator.authenticate(null, mockResponse)
        tokenAuthenticator.authenticate(null, mockResponse)
        tokenAuthenticator.authenticate(null, mockResponse)

        // then
        assertTrue(logoutEvents.size == 3)

        // cleanup
        job.cancel()
    }

    @Test
    fun `여러 구독자가 동시에 로그아웃 이벤트를 받는다`() = runTest {
        // Given
        val events1 = mutableListOf<Unit>()
        val events2 = mutableListOf<Unit>()
        val events3 = mutableListOf<Unit>()

        val job1 = launch(UnconfinedTestDispatcher(testScheduler)) {
            authEventBus.logoutEvent.collect { events1.add(it) }
        }
        val job2 = launch(UnconfinedTestDispatcher(testScheduler)) {
            authEventBus.logoutEvent.collect { events2.add(it) }
        }
        val job3 = launch(UnconfinedTestDispatcher(testScheduler)) {
            authEventBus.logoutEvent.collect { events3.add(it) }
        }

        val mockResponse = mockk<Response>()
        coEvery {
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.RefreshToken)
        } returns flowOf(null)

        // When
        tokenAuthenticator.authenticate(null, mockResponse)

        // Then: 모든 구독자가 이벤트 수신
        assertTrue(events1.size == 1)
        assertTrue(events2.size == 1)
        assertTrue(events3.size == 1)

        job1.cancel()
        job2.cancel()
        job3.cancel()
    }

    private fun verifyDataDeletion() {
        coVerify { dataStoreManager.deleteStringData(DataStoreKey.Auth.AccessToken) }
        coVerify { dataStoreManager.deleteStringData(DataStoreKey.Auth.RefreshToken) }
        coVerify { dataStoreManager.deleteLongData(DataStoreKey.User.UserId) }
    }
}
