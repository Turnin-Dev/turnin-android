package com.peekr.core.data.source.network.retrofit

import com.peekr.core.data.eventBus.AuthEventBus
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
    private val authEventBus: AuthEventBus = mockk()
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

        tokenAuthenticator = TokenAuthenticator(dataStoreManager, refreshTokenApi, authEventBus)

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

        // AuthEventBus mock 생성
        coEvery { authEventBus.emitLogout() } just Runs
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        clearAllMocks()
    }

    @Test
    fun `토큰 갱신 성공 시 새 토큰으로 요청 생성`() = runTest {
        // Given
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

        // When
        val result = tokenAuthenticator.authenticate(null, unauthorizedResponse)

        // Then
        assertNotNull(result)

        // 리프레쉬 토큰으로 올바르게 요청했는지 확인
        coVerify(exactly = 1) { refreshTokenApi.refresh("Bearer $OLD_REFRESH_TOKEN") }

        // 새로운 토큰이 저장되었는지 확인
        coVerify(exactly = 1) {
            dataStoreManager.saveEncryptedStringData(DataStoreKey.Auth.AccessToken, NEW_ACCESS_TOKEN)
        }
        coVerify(exactly = 1) {
            dataStoreManager.saveEncryptedStringData(DataStoreKey.Auth.RefreshToken, NEW_REFRESH_TOKEN)
        }

        // 헤더가 올바르게 추가되었는지 확인
        assertEquals("Bearer $NEW_ACCESS_TOKEN", result?.header("Authorization"))
    }

    @Test
    fun `기존 리프레쉬 토큰이 없으면 인증 관련 데이터 삭제, 로그아웃 처리 후 null 반환`() = runTest {
        // Given: 기존 리프레쉬 토큰이 없게끔 세팅
        coEvery {
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.AccessToken)
        } returns flowOf(OLD_ACCESS_TOKEN)
        coEvery {
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.RefreshToken)
        } returns flowOf(null)
        coEvery { dataStoreManager.deleteStringData(DataStoreKey.Auth.AccessToken) } just Runs
        coEvery { dataStoreManager.deleteStringData(DataStoreKey.Auth.RefreshToken) } just Runs
        coEvery { dataStoreManager.deleteLongData(DataStoreKey.User.UserId) } just Runs
        coEvery { authEventBus.emitLogout() } just Runs

        // When
        val result = tokenAuthenticator.authenticate(null, unauthorizedResponse)

        // Then
        assertNull(result)

        // 인증 관련 데이터 삭제 및 로그아웃 처리 확인
        coVerify(exactly = 1) { dataStoreManager.deleteStringData(DataStoreKey.Auth.AccessToken) }
        coVerify(exactly = 1) { dataStoreManager.deleteStringData(DataStoreKey.Auth.RefreshToken) }
        coVerify(exactly = 1) { dataStoreManager.deleteLongData(DataStoreKey.User.UserId) }
        coVerify(exactly = 1) { authEventBus.emitLogout() }

        // 새로운 토큰 저장이 호출되지 않았는지 확인
        coVerify(exactly = 0) { dataStoreManager.saveEncryptedStringData(any(), any()) }
    }

    @Test
    fun `토큰 갱신 API가 실패하면 기존 토큰 삭제, 로그아웃 처리 후 null 반환`() = runTest {
        // Given: 리프레쉬 토큰 갱신 API가 실패하게끔 세팅
        coEvery {
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.AccessToken)
        } returns flowOf(OLD_ACCESS_TOKEN)
        coEvery {
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.RefreshToken)
        } returns flowOf(OLD_REFRESH_TOKEN)
        coEvery { dataStoreManager.deleteStringData(DataStoreKey.Auth.AccessToken) } just Runs
        coEvery { dataStoreManager.deleteStringData(DataStoreKey.Auth.RefreshToken) } just Runs
        coEvery { dataStoreManager.deleteLongData(DataStoreKey.User.UserId) } just Runs
        coEvery { authEventBus.emitLogout() } just Runs
        coEvery { refreshTokenApi.refresh("Bearer $OLD_REFRESH_TOKEN") } returns retrofit2.Response.error(
            404,
            "".toResponseBody("application/json".toMediaTypeOrNull()),
        )

        // When
        val result = tokenAuthenticator.authenticate(null, unauthorizedResponse)

        // Then
        assertNull(result)

        // 인증 관련 데이터 삭제 및 로그아웃 처리 확인
        coVerify(exactly = 1) { refreshTokenApi.refresh("Bearer $OLD_REFRESH_TOKEN") }
        coVerify(exactly = 1) { dataStoreManager.deleteStringData(DataStoreKey.Auth.AccessToken) }
        coVerify(exactly = 1) { dataStoreManager.deleteStringData(DataStoreKey.Auth.RefreshToken) }
        coVerify(exactly = 1) { dataStoreManager.deleteLongData(DataStoreKey.User.UserId) }
        coVerify(exactly = 1) { authEventBus.emitLogout() }

        // 새로운 토큰 저장이 호출되지 않았는지 확인
        coVerify(exactly = 0) { dataStoreManager.saveEncryptedStringData(any(), any()) }
    }

    @Test
    fun `토큰 갱신 응답 성공이지만 body가 비어있는 경우 기존 토큰 삭제, 로그아웃 처리 후 null 반환`() = runTest {
        // Given: 리프레쉬 토큰 갱신 API가 성공하지만 body가 비어있게끔 설정
        coEvery {
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.AccessToken)
        } returns flowOf(OLD_ACCESS_TOKEN)
        coEvery {
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.RefreshToken)
        } returns flowOf(OLD_REFRESH_TOKEN)
        coEvery { dataStoreManager.deleteStringData(DataStoreKey.Auth.AccessToken) } just Runs
        coEvery { dataStoreManager.deleteStringData(DataStoreKey.Auth.RefreshToken) } just Runs
        coEvery { dataStoreManager.deleteLongData(DataStoreKey.User.UserId) } just Runs
        coEvery { authEventBus.emitLogout() } just Runs
        coEvery {
            refreshTokenApi.refresh("Bearer $OLD_REFRESH_TOKEN")
        } returns retrofit2.Response.success(null)

        // When
        val result = tokenAuthenticator.authenticate(null, unauthorizedResponse)

        // Then
        assertNull(result)

        // 인증 관련 데이터 삭제 및 로그아웃 처리 확인
        coVerify(exactly = 1) { refreshTokenApi.refresh("Bearer $OLD_REFRESH_TOKEN") }
        coVerify(exactly = 1) { dataStoreManager.deleteStringData(DataStoreKey.Auth.AccessToken) }
        coVerify(exactly = 1) { dataStoreManager.deleteStringData(DataStoreKey.Auth.RefreshToken) }
        coVerify(exactly = 1) { dataStoreManager.deleteLongData(DataStoreKey.User.UserId) }
        coVerify(exactly = 1) { authEventBus.emitLogout() }

        // 새로운 토큰 저장이 호출되지 않았는지 확인
        coVerify(exactly = 0) { dataStoreManager.saveEncryptedStringData(any(), any()) }
    }

    @Test
    fun `다른 스레드에서 이미 토큰을 갱신했으면 새 토큰으로 요청 생성`() = runTest {
        // Given
        val newAccessToken = "new.access.token"
        val oldRequestToken = "Bearer old.access.token"

        // 요청에 포함된 토큰
        val requestWithOldToken = Request
            .Builder()
            .url(mockWebServer.url("/test"))
            .header("Authorization", oldRequestToken)
            .build()

        val responseWithOldToken = Response
            .Builder()
            .request(requestWithOldToken)
            .protocol(Protocol.HTTP_1_1)
            .code(401)
            .message("Unauthorized")
            .build()

        // DataStore에는 이미 새 토큰이 저장되어 있음
        coEvery {
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.AccessToken)
        } returns flowOf(newAccessToken)

        // When
        val result = tokenAuthenticator.authenticate(null, responseWithOldToken)

        // Then
        assertNotNull(result)
        assertEquals("Bearer $newAccessToken", result?.header("Authorization"))

        // API 호출이 일어나지 않아야 함
        coVerify(exactly = 0) { refreshTokenApi.refresh(any()) }
        coVerify(exactly = 0) { dataStoreManager.saveEncryptedStringData(any(), any()) }
    }

    @Test
    fun `네트워크 오류 시 예외 전파`() = runTest {
        // Given: refresh API 호출 시 예외가 발생하도록 세팅
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
            refreshTokenApi.refresh(any())
        } throws IOException("Network error")

        // When
        val exception = runCatching {
            tokenAuthenticator.authenticate(null, unauthorizedResponse)
        }.exceptionOrNull()

        // Then
        assertNotNull(exception)
        assertTrue(exception is IOException)

        // 토큰 관련 작업이 수행되지 않았는지 확인
        coVerify(exactly = 0) { dataStoreManager.saveEncryptedStringData(any(), any()) }
        coVerify(exactly = 0) { dataStoreManager.deleteStringData(any()) }
    }

    @Test
    fun `일반 예외 발생 시 예외 전파`() = runTest {
        // Given: refresh API 호출 시 일반 예외가 발생하도록 세팅
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
            refreshTokenApi.refresh(any())
        } throws RuntimeException("unexpected error")

        // When
        val exception = runCatching {
            tokenAuthenticator.authenticate(null, unauthorizedResponse)
        }.exceptionOrNull()

        // Then
        assertNotNull(exception)
        assertTrue(exception is RuntimeException)

        // 토큰 관련 작업이 수행되지 않았는지 확인
        coVerify(exactly = 0) { dataStoreManager.saveEncryptedStringData(any(), any()) }
        coVerify(exactly = 0) { dataStoreManager.deleteStringData(any()) }
    }

    companion object {
        private const val OLD_ACCESS_TOKEN = "old.access.token"
        private const val OLD_REFRESH_TOKEN = "old.refresh.token"
        private const val NEW_ACCESS_TOKEN = "new.refresh.token"
        private const val NEW_REFRESH_TOKEN = "new.refresh.token"
    }
}
