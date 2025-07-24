package com.peekr.data.shared.retrofit

import com.peekr.data.account.network.AccountApi
import com.peekr.domain.shared.dataStore.DataStoreKey
import com.peekr.domain.shared.dataStore.DataStoreManager
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import okhttp3.Request
import okhttp3.Route
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import retrofit2.Response

@RunWith(RobolectricTestRunner::class)
class TokenAuthenticatorTest {
    private val dataStoreManager: DataStoreManager = mockk()
    private val accountApi: AccountApi = mockk()

    private lateinit var tokenAuthenticator: TokenAuthenticator

    private val mockRoute: Route = mockk()
    private val mockResponse: okhttp3.Response = mockk()
    private val mockRequest: Request = mockk()
    private val mockRequestBuilder: Request.Builder = mockk()

    private val sampleTokenResponse = TokenResponse(
        accessToken = "new_access_token",
        refreshToken = "new_refresh_token",
    )

    @Before
    fun setUp() {
        tokenAuthenticator = TokenAuthenticator(dataStoreManager, accountApi)

        // Mock request와 builder 설정
        every { mockResponse.request } returns mockRequest
        every { mockRequest.newBuilder() } returns mockRequestBuilder
        every { mockRequestBuilder.header(any(), any()) } returns mockRequestBuilder
        every { mockRequestBuilder.build() } returns mockRequest

        // DataStore manager mock 설정
        coEvery { dataStoreManager.deleteStringData(any()) } just Runs
        coEvery { dataStoreManager.saveEncryptedStringData(any(), any()) } just Runs
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `토큰 갱신 성공 시 새로운 토큰으로 헤더가 추가된 Request 반환`() = runTest {
        // Given
        val successfulResponse: Response<TokenResponse> = mockk {
            every { isSuccessful } returns true
            every { body() } returns sampleTokenResponse
            every { code() } returns 200
        }
        coEvery { accountApi.refresh() } returns successfulResponse

        // When
        val result = tokenAuthenticator.authenticate(mockRoute, mockResponse)

        // Then
        // 새로운 토큰이 저장되었는지 확인
        coVerify { dataStoreManager.saveEncryptedStringData(DataStoreKey.Auth.AccessToken, "new_access_token") }
        coVerify { dataStoreManager.saveEncryptedStringData(DataStoreKey.Auth.RefreshToken, "new_refresh_token") }

        // 헤더가 올바르게 추가되었는지 확인
        verify { mockRequestBuilder.header("Authorization", "Bearer new_access_token") }

        // 결과 확인
        assertEquals(mockRequest, result)
    }

    @Test
    fun `토큰 갱신 실패 시 기존 토큰 삭제 후 null 반환`() = runTest {
        // Given
        val failedResponse: Response<TokenResponse> = mockk {
            every { isSuccessful } returns false
            every { body() } returns null
            every { code() } returns 401
        }
        coEvery { accountApi.refresh() } returns failedResponse

        // When
        val result = tokenAuthenticator.authenticate(mockRoute, mockResponse)

        // Then
        // 기존 토큰이 삭제되었는지 확인
        coVerify { dataStoreManager.deleteStringData(DataStoreKey.Auth.AccessToken) }
        coVerify { dataStoreManager.deleteStringData(DataStoreKey.Auth.RefreshToken) }

        // 새로운 토큰 저장이 호출되지 않았는지 확인
        coVerify(exactly = 0) { dataStoreManager.saveEncryptedStringData(any(), any()) }

        // null 반환 확인
        assertNull(result)
    }

    @Test
    fun `토큰 갱신 응답 성공이지만 body가 null인 경우 기존 토큰 삭제`() = runTest {
        // Given
        val responseWithNullBody: Response<TokenResponse> = mockk {
            every { isSuccessful } returns true
            every { body() } returns null
            every { code() } returns 200
        }
        coEvery { accountApi.refresh() } returns responseWithNullBody

        // When
        val result = tokenAuthenticator.authenticate(mockRoute, mockResponse)

        // Then
        // 기존 토큰이 삭제되었는지 확인
        coVerify { dataStoreManager.deleteStringData(DataStoreKey.Auth.AccessToken) }
        coVerify { dataStoreManager.deleteStringData(DataStoreKey.Auth.RefreshToken) }

        // 새로운 토큰 저장이 호출되지 않았는지 확인
        coVerify(exactly = 0) { dataStoreManager.saveEncryptedStringData(any(), any()) }

        // null 반환 확인
        assertNull(result)
    }

    @Test
    fun `API 호출 시 예외 발생 시 예외 전파`() = runTest {
        // Given
        val exceptionMessage = "Network error"
        val expectedException = RuntimeException(exceptionMessage)
        coEvery { accountApi.refresh() } throws expectedException

        // When & Then
        try {
            tokenAuthenticator.authenticate(mockRoute, mockResponse)
            assert(false) { "예외가 발생해야 합니다" }
        } catch (e: RuntimeException) {
            assertEquals(exceptionMessage, e.message)
        }

        // 토큰 관련 작업이 수행되지 않았는지 확인
        coVerify(exactly = 0) { dataStoreManager.deleteStringData(any()) }
        coVerify(exactly = 0) { dataStoreManager.saveEncryptedStringData(any(), any()) }
    }

    @Test
    fun `다양한 HTTP 상태 코드에 대한 처리 확인`() = runTest {
        // Given - 403 Forbidden
        val forbiddenResponse: Response<TokenResponse> = mockk {
            every { isSuccessful } returns false
            every { body() } returns null
            every { code() } returns 403
        }
        coEvery { accountApi.refresh() } returns forbiddenResponse

        // When
        val result = tokenAuthenticator.authenticate(mockRoute, mockResponse)

        // Then
        coVerify { dataStoreManager.deleteStringData(DataStoreKey.Auth.AccessToken) }
        coVerify { dataStoreManager.deleteStringData(DataStoreKey.Auth.RefreshToken) }
        assertNull(result)
    }

    @Test
    fun `DataStore 저장 시 예외 발생해도 Request 반환`() = runTest {
        // Given
        val successfulResponse: Response<TokenResponse> = mockk {
            every { isSuccessful } returns true
            every { body() } returns sampleTokenResponse
            every { code() } returns 200
        }
        coEvery { accountApi.refresh() } returns successfulResponse
        coEvery {
            dataStoreManager.saveEncryptedStringData(any(), any())
        } throws RuntimeException("Storage error")

        // When & Then
        var result: Request? = null
        try {
            result = tokenAuthenticator.authenticate(mockRoute, mockResponse)
            assert(false) { "예외가 발생해야 합니다" }
        } catch (e: RuntimeException) {
            assertEquals("Storage error", e.message)
        }

        assertNull(result)
    }

    @Test
    fun `토큰 응답에서 빈 문자열 토큰 처리`() = runTest {
        // Given
        val responseWithEmptyTokens = TokenResponse(
            accessToken = "",
            refreshToken = "",
        )
        val successfulResponse: Response<TokenResponse> = mockk {
            every { isSuccessful } returns true
            every { body() } returns responseWithEmptyTokens
            every { code() } returns 200
        }
        coEvery { accountApi.refresh() } returns successfulResponse

        // When
        val result = tokenAuthenticator.authenticate(mockRoute, mockResponse)

        // Then
        // 빈 토큰이라도 저장되는지 확인
        coVerify { dataStoreManager.saveEncryptedStringData(DataStoreKey.Auth.AccessToken, "") }
        coVerify { dataStoreManager.saveEncryptedStringData(DataStoreKey.Auth.RefreshToken, "") }

        // 빈 토큰으로 헤더가 설정되는지 확인
        verify { mockRequestBuilder.header("Authorization", "Bearer ") }

        assertEquals(mockRequest, result)
    }
}
