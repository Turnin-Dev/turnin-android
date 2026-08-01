package com.turnin.core.data.source.network.retrofit

import com.turnin.core.data.MockLog
import com.turnin.core.data.eventBus.AuthEventBusImpl
import com.turnin.core.data.source.local.datastore.DataStoreKey
import com.turnin.core.data.source.local.datastore.DataStoreManager
import com.turnin.core.data.source.network.api.NetworkApiPath
import com.turnin.core.data.source.network.api.RefreshTokenApi
import com.turnin.core.domain.eventBus.AuthEventBus
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TokenAuthenticatorLogoutTest {
    private val dataStoreManager: DataStoreManager = mockk()
    private val refreshTokenApi: RefreshTokenApi = mockk()
    private val authEventBus: AuthEventBus = AuthEventBusImpl()
    private lateinit var tokenAuthenticator: TokenAuthenticator
    private lateinit var mockWebServer: MockWebServer
    private lateinit var unauthorizedResponse: Response
    private lateinit var testRequest: Request

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        MockLog.mock()

        // MockWebServer 설정
        mockWebServer = MockWebServer()
        mockWebServer.start()

        Dispatchers.setMain(testDispatcher)

        tokenAuthenticator = TokenAuthenticator(
            dataStoreManager,
            refreshTokenApi,
            authEventBus,
        )

        // mock 요청, 응답 설정
        // 테스트용 Request 생성
        testRequest = Request
            .Builder()
            .url(mockWebServer.url("/test"))
            .header("Authorization", "Bearer $OLD_ACCESS_TOKEN")
            .build()

        // 401 에러 Response 모킹
        unauthorizedResponse = Response
            .Builder()
            .request(testRequest)
            .protocol(Protocol.HTTP_1_1)
            .code(401)
            .message("Unauthorized")
            .build()

        // mock 설정
        coEvery { dataStoreManager.deleteStringData(DataStoreKey.Auth.AccessToken) } just Runs
        coEvery { dataStoreManager.deleteStringData(DataStoreKey.Auth.RefreshToken) } just Runs
        coEvery { dataStoreManager.deleteLongData(DataStoreKey.User.UserId) } just Runs
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
        mockWebServer.close()

        clearAllMocks()
        MockLog.cleanUp()
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

        coEvery {
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.AccessToken)
        } returns flowOf(OLD_ACCESS_TOKEN)
        coEvery {
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.RefreshToken)
        } returns flowOf(null)

        // when
        val result = tokenAuthenticator.authenticate(null, unauthorizedResponse)

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
        coEvery {
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.AccessToken)
        } returns flowOf(OLD_ACCESS_TOKEN)
        coEvery {
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.RefreshToken)
        } returns flowOf(OLD_REFRESH_TOKEN)
        coEvery {
            refreshTokenApi.refresh("Bearer $OLD_REFRESH_TOKEN")
        } returns retrofit2.Response.error(
            401,
            "".toResponseBody("application/json".toMediaTypeOrNull()),
        )

        // when
        val result = tokenAuthenticator.authenticate(null, unauthorizedResponse)

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
        coEvery {
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.AccessToken)
        } returns flowOf(OLD_ACCESS_TOKEN)
        coEvery {
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.RefreshToken)
        } returns flowOf(OLD_REFRESH_TOKEN)
        coEvery {
            refreshTokenApi.refresh("Bearer $OLD_REFRESH_TOKEN")
        } returns retrofit2.Response.success(null)

        // when
        val result = tokenAuthenticator.authenticate(null, unauthorizedResponse)

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
        val tokenResponse = TokenResponse(NEW_ACCESS_TOKEN, NEW_REFRESH_TOKEN)
        val successResponse = retrofit2.Response.success(tokenResponse)
        coEvery { refreshTokenApi.refresh(any()) } returns successResponse
        coEvery {
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.AccessToken)
        } returns flowOf(OLD_ACCESS_TOKEN)
        coEvery {
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.RefreshToken)
        } returns flowOf(OLD_REFRESH_TOKEN)
        coEvery {
            dataStoreManager.saveEncryptedStringData(DataStoreKey.Auth.AccessToken, NEW_ACCESS_TOKEN)
        } just Runs
        coEvery {
            dataStoreManager.saveEncryptedStringData(DataStoreKey.Auth.RefreshToken, NEW_REFRESH_TOKEN)
        } just Runs

        // when
        val result = tokenAuthenticator.authenticate(null, unauthorizedResponse)

        // then
        assertNotNull(result)
        assertTrue(logoutEvents.isEmpty())
        coVerify {
            dataStoreManager.saveEncryptedStringData(
                DataStoreKey.Auth.AccessToken,
                NEW_ACCESS_TOKEN,
            )
        }
        coVerify {
            dataStoreManager.saveEncryptedStringData(
                DataStoreKey.Auth.RefreshToken,
                NEW_REFRESH_TOKEN,
            )
        }

        // cleanup
        job.cancel()
    }

    @Test
    fun `여러 번 실패해도 매번 로그아웃 이벤트가 발생한다`() = runTest {
        // given
        val logoutEvents = mutableListOf<Unit>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            authEventBus.logoutEvent.collect {
                logoutEvents.add(it)
            }
        }

        setFailure()

        // when
        tokenAuthenticator.authenticate(null, unauthorizedResponse)
        tokenAuthenticator.authenticate(null, unauthorizedResponse)
        tokenAuthenticator.authenticate(null, unauthorizedResponse)

        // then
        assertTrue(logoutEvents.size == 3)

        // cleanup
        job.cancel()
    }

    @Test
    fun `로그아웃 요청에서 401이 발생해도 인증 루프를 중단하고 null을 반환한다`() = runTest {
        // given
        val logoutEvents = mutableListOf<Unit>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            authEventBus.logoutEvent.collect {
                logoutEvents.add(it)
            }
        }

        val logoutRequest = Request
            .Builder()
            .url(mockWebServer.url(NetworkApiPath.User.LOGOUT))
            .header("Authorization", "Bearer $OLD_ACCESS_TOKEN")
            .build()

        val logoutUnauthorizedResponse = Response
            .Builder()
            .request(logoutRequest)
            .protocol(Protocol.HTTP_1_1)
            .code(401)
            .message("Unauthorized")
            .build()

        // when
        val result = tokenAuthenticator.authenticate(null, logoutUnauthorizedResponse)

        // then
        assertNull(result)
        assertTrue("로그아웃 요청 중 401은 루프를 유발하지 않아야 한다", logoutEvents.isEmpty())
        coVerify(exactly = 0) { dataStoreManager.deleteStringData(any()) }
        coVerify(exactly = 0) { dataStoreManager.deleteLongData(any()) }

        // cleanup
        job.cancel()
    }

    // 기존 리프레쉬 토큰을 없도록 설정하여 실패를 유도한다.
    private fun setFailure() {
        coEvery {
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.AccessToken)
        } returns flowOf(OLD_ACCESS_TOKEN)
        coEvery {
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.RefreshToken)
        } returns flowOf(null)
    }

    private fun verifyDataDeletion() {
        coVerify { dataStoreManager.deleteStringData(DataStoreKey.Auth.AccessToken) }
        coVerify { dataStoreManager.deleteStringData(DataStoreKey.Auth.RefreshToken) }
        coVerify { dataStoreManager.deleteLongData(DataStoreKey.User.UserId) }
    }

    companion object {
        private const val OLD_ACCESS_TOKEN = "old.access.token"
        private const val OLD_REFRESH_TOKEN = "old.refresh.token"
        private const val NEW_ACCESS_TOKEN = "new.access.token"
        private const val NEW_REFRESH_TOKEN = "new.refresh.token"
    }
}
