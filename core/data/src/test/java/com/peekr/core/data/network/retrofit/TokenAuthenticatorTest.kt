package com.peekr.core.data.network.retrofit

import com.peekr.core.data.source.local.datastore.DataStoreKey
import com.peekr.core.data.source.local.datastore.DataStoreManager
import com.peekr.core.data.source.network.api.RefreshTokenApi
import com.peekr.core.data.source.network.retrofit.TokenAuthenticator
import com.peekr.core.data.source.network.retrofit.TokenResponse
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.mockwebserver.MockWebServer
import okio.IOException
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TokenAuthenticatorTest {
    private val dataStoreManager: DataStoreManager = mockk()
    private val refreshTokenApi: RefreshTokenApi = mockk()
    private lateinit var mockWebServer: MockWebServer
    private lateinit var tokenAuthenticator: TokenAuthenticator
    private lateinit var unauthorizedResponse: Response

    // 실제 Request 객체들
    private lateinit var testRequest: Request

    @Before
    fun setUp() {
        // MockWebServer 설정
        mockWebServer = MockWebServer()
        mockWebServer.start()

        tokenAuthenticator = TokenAuthenticator(dataStoreManager, refreshTokenApi)

        // 테스트용 Request 생성
        testRequest = Request
            .Builder()
            .url(mockWebServer.url("/test"))
            .build()

        // 401 에러 Response 모킹
        unauthorizedResponse = Response
            .Builder()
            .request(testRequest)
            .protocol(Protocol.HTTP_1_1)
            .code(401)
            .message("Unauthorized")
            .build()

        // DataStore manager mock 설정
        coEvery { dataStoreManager.getEncryptedStringData(any()) } returns flowOf("original.token")
        coEvery { dataStoreManager.deleteStringData(any()) } just Runs
        coEvery { dataStoreManager.saveEncryptedStringData(any(), any()) } just Runs
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        clearAllMocks()
    }

    @Test
    fun `토큰 갱신 성공 시 새로운 토큰으로 헤더가 추가된 Request 반환`() = runTest {
        // Given
        val newAccessToken = "newAccessToken"
        val newRefreshToken = "newRefreshToken"
        coEvery {
            refreshTokenApi.refresh(any())
        } returns retrofit2.Response.success(TokenResponse(newAccessToken, newRefreshToken))

        // When
        val result = tokenAuthenticator.authenticate(null, unauthorizedResponse)

        // Then
        assertNotNull(result)

        // 새로운 토큰이 저장되었는지 확인
        coVerify { dataStoreManager.saveEncryptedStringData(DataStoreKey.Auth.AccessToken, newAccessToken) }
        coVerify { dataStoreManager.saveEncryptedStringData(DataStoreKey.Auth.RefreshToken, newRefreshToken) }

        // 헤더가 올바르게 추가되었는지 확인
        assertEquals("Bearer $newAccessToken", result?.header("Authorization"))
    }

    @Test
    fun `토큰 갱신 실패 시 기존 토큰 삭제 후 null 반환`() = runTest {
        // Given
        coEvery {
            refreshTokenApi.refresh(any())
        } returns retrofit2.Response.error(
            404,
            "".toResponseBody("application/json".toMediaTypeOrNull()),
        )

        // When
        val result = tokenAuthenticator.authenticate(null, unauthorizedResponse)

        // Then
        assertNull(result)

        // 기존 토큰이 삭제되었는지 확인
        coVerify { dataStoreManager.deleteStringData(DataStoreKey.Auth.AccessToken) }
        coVerify { dataStoreManager.deleteStringData(DataStoreKey.Auth.RefreshToken) }

        // 새로운 토큰 저장이 호출되지 않았는지 확인
        coVerify(exactly = 0) { dataStoreManager.saveEncryptedStringData(any(), any()) }
    }

    @Test
    fun `토큰 갱신 응답 성공이지만 body가 비어있는 경우 기존 토큰 삭제`() = runTest {
        // Given
        coEvery {
            refreshTokenApi.refresh(any())
        } returns retrofit2.Response.success(null)

        // When
        val result = tokenAuthenticator.authenticate(null, unauthorizedResponse)

        // Then
        assertNull(result)

        // 기존 토큰이 삭제되었는지 확인
        coVerify { dataStoreManager.deleteStringData(DataStoreKey.Auth.AccessToken) }
        coVerify { dataStoreManager.deleteStringData(DataStoreKey.Auth.RefreshToken) }

        // 새로운 토큰 저장이 호출되지 않았는지 확인
        coVerify(exactly = 0) { dataStoreManager.saveEncryptedStringData(any(), any()) }
    }

    @Test
    fun `네트워크 오류 시 예외 전파`() = runTest {
        // Given
        coEvery { refreshTokenApi.refresh(any()) } throws IOException()

        // When & Then
        try {
            tokenAuthenticator.authenticate(null, unauthorizedResponse)
            assert(false) { "예외가 발생해야 합니다" }
        } catch (e: Exception) {
            // 네트워크 관련 예외가 발생해야 함
            assertNotNull(e)
        }

        // 토큰 관련 작업이 수행되지 않았는지 확인
        coVerify(exactly = 0) { dataStoreManager.deleteStringData(any()) }
        coVerify(exactly = 0) { dataStoreManager.saveEncryptedStringData(any(), any()) }
    }

    @Test
    fun `토큰 응답에서 빈 문자열 토큰 처리`() = runTest {
        // Given
        coEvery {
            refreshTokenApi.refresh(any())
        } returns retrofit2.Response.success(TokenResponse("", ""))

        // When
        val result = tokenAuthenticator.authenticate(null, unauthorizedResponse)

        // Then
        assertNotNull(result)

        // 빈 토큰이라도 저장되는지 확인
        coVerify { dataStoreManager.saveEncryptedStringData(DataStoreKey.Auth.AccessToken, "") }
        coVerify { dataStoreManager.saveEncryptedStringData(DataStoreKey.Auth.RefreshToken, "") }

        // 빈 토큰으로 헤더가 설정되는지 확인
        assertTrue(result!!.header("Authorization")!!.startsWith("Bearer"))
    }

    @Test
    fun `기존 토큰이 없거나 가져오지 못했을 경우 갱신 실패 처리를 한다`() = runTest {
        // Given
        val newAccessToken = "newAccessToken"
        val newRefreshToken = "newRefreshToken"
        coEvery { dataStoreManager.getEncryptedStringData(any()) } returns flowOf(null)
        coEvery {
            refreshTokenApi.refresh(any())
        } returns retrofit2.Response.success(TokenResponse(newAccessToken, newRefreshToken))

        // When
        val result = tokenAuthenticator.authenticate(null, unauthorizedResponse)

        // Then
        assertNull(result)
    }
}
