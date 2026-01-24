package com.peekr.core.data.source.network.retrofit

import com.peekr.core.common.logger.AppLogger
import com.peekr.core.data.source.local.datastore.DataStoreKey
import com.peekr.core.data.source.local.datastore.DataStoreManager
import com.peekr.core.data.source.network.retrofit.RetrofitConstants
import com.peekr.core.data.source.network.retrofit.TokenInterceptor
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TokenInterceptorTest {
    private val tag = TokenInterceptor::class.java.simpleName
    private val dataStoreManager: DataStoreManager = mockk()
    private val chain: Interceptor.Chain = mockk()

    private lateinit var tokenInterceptor: TokenInterceptor

    private val mockRequest: Request = mockk()
    private val mockRequestBuilder: Request.Builder = mockk()
    private val mockResponse: Response = mockk()
    private val mockRequestWithToken: Request = mockk()

    @Before
    fun setUp() {
        tokenInterceptor = TokenInterceptor(dataStoreManager)

        // Mock 기본 설정
        every { chain.request() } returns mockRequest
        every { mockRequest.newBuilder() } returns mockRequestBuilder
        every { mockRequestBuilder.addHeader(any(), any()) } returns mockRequestBuilder
        every { mockRequestBuilder.build() } returns mockRequestWithToken
        every { mockRequest.method } returns "GET"
        every { mockRequest.url } returns "https://example.com/test".toHttpUrl()

        // 기본적인 chain.proceed mock 설정
        every { chain.proceed(any()) } returns mockResponse

        // Timber 로깅 mock
        mockkObject(AppLogger)
        every { AppLogger.d(any(), any()) } just Runs
        every { AppLogger.w(any(), any()) } just Runs
        every { AppLogger.e(any(), any()) } just Runs
    }

    @After
    fun tearDown() {
        clearAllMocks()
        unmockkStatic(AppLogger::class)
    }

    @Test
    fun `액세스 토큰이 있는 경우 헤더에 토큰 추가하여 요청 진행`() = runTest {
        // Given
        val accessToken = "valid_access_token"
        every {
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.AccessToken)
        } returns flowOf(accessToken)
        every { chain.proceed(mockRequestWithToken) } returns mockResponse
        every { mockResponse.isSuccessful } returns true
        every { mockResponse.code } returns 200

        // When
        val result = tokenInterceptor.intercept(chain)

        // Then
        verify {
            mockRequestBuilder.addHeader(
                RetrofitConstants.AUTHENTICATION,
                "${RetrofitConstants.BEARER} $accessToken",
            )
        }
        verify { chain.proceed(mockRequestWithToken) }
        verify { AppLogger.d(tag, "Response Success (200)") }
        assertEquals(mockResponse, result)
    }

    @Test
    fun `액세스 토큰이 null인 경우 원본 요청으로 바로 진행`() = runTest {
        // Given
        every {
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.AccessToken)
        } returns flowOf(null)

        // When
        val result = tokenInterceptor.intercept(chain)

        // Then
        verify(exactly = 0) { mockRequestBuilder.addHeader(any(), any()) }
        verify { chain.proceed(mockRequest) }
        assertEquals(mockResponse, result)
    }

    @Test
    fun `액세스 토큰이 빈 문자열인 경우 원본 요청으로 바로 진행`() = runTest {
        // Given
        every {
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.AccessToken)
        } returns flowOf("")

        // When
        val result = tokenInterceptor.intercept(chain)

        // Then
        verify(exactly = 0) { mockRequestBuilder.addHeader(any(), any()) }
        verify { chain.proceed(mockRequest) }
        assertEquals(mockResponse, result)
    }

    @Test
    fun `DataStore에서 데이터 조회 시 null인 경우 원본 요청 진행`() = runTest {
        // Given
        every {
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.AccessToken)
        } returns flowOf<String?>(null)

        // When
        val result = tokenInterceptor.intercept(chain)

        // Then
        verify(exactly = 0) { mockRequestBuilder.addHeader(any(), any()) }
        verify { chain.proceed(mockRequest) }
        assertEquals(mockResponse, result)
    }

    @Test
    fun `응답이 성공(200)인 경우 성공 로그 출력`() = runTest {
        // Given
        val accessToken = "valid_token"
        every {
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.AccessToken)
        } returns flowOf(accessToken)
        every { chain.proceed(mockRequestWithToken) } returns mockResponse
        every { mockResponse.isSuccessful } returns true
        every { mockResponse.code } returns 200

        // When
        tokenInterceptor.intercept(chain)

        // Then
        verify { AppLogger.d(tag, "Response Success (200)") }
    }

    @Test
    fun `응답이 성공(201)인 경우 성공 로그 출력`() = runTest {
        // Given
        val accessToken = "valid_token"
        every {
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.AccessToken)
        } returns flowOf(accessToken)
        every { chain.proceed(mockRequestWithToken) } returns mockResponse
        every { mockResponse.isSuccessful } returns true
        every { mockResponse.code } returns 201

        // When
        tokenInterceptor.intercept(chain)

        // Then
        verify { AppLogger.d(tag, "Response Success (201)") }
    }

    @Test
    fun `응답이 실패(404)인 경우 실패 로그 출력`() = runTest {
        // Given
        val accessToken = "valid_token"
        every {
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.AccessToken)
        } returns flowOf(accessToken)
        every { chain.proceed(mockRequestWithToken) } returns mockResponse
        every { mockResponse.isSuccessful } returns false
        every { mockResponse.code } returns 404
        every { mockResponse.request } returns mockRequest
        every { mockResponse.message } returns "Not Found"

        // When
        tokenInterceptor.intercept(chain)

        // Then
        verify { AppLogger.w(tag, "Response Client Error (404)") }
    }

    @Test
    fun `응답이 성공이지만 특정 코드가 아닌 경우 일반 성공 로그 출력`() = runTest {
        // Given
        val accessToken = "valid_token"
        every {
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.AccessToken)
        } returns flowOf(accessToken)
        every { chain.proceed(mockRequestWithToken) } returns mockResponse
        every { mockResponse.isSuccessful } returns true
        every { mockResponse.code } returns 202

        // When
        tokenInterceptor.intercept(chain)

        // Then
        verify { AppLogger.d(tag, "Response Success (202)") }
    }

    @Test
    fun `응답이 실패이고 특정 코드가 아닌 경우 일반 실패 로그 출력`() = runTest {
        // Given
        val accessToken = "valid_token"
        every {
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.AccessToken)
        } returns flowOf(accessToken)
        every { chain.proceed(mockRequestWithToken) } returns mockResponse
        every { mockResponse.isSuccessful } returns false
        every { mockResponse.code } returns 500
        every { mockResponse.request } returns mockRequest
        every { mockResponse.message } returns "Internal Server Error"

        // When
        tokenInterceptor.intercept(chain)

        // Then
        verify { AppLogger.e(tag, "Response Server Error (500)") }
    }

    @Test
    fun `401 Unauthorized 응답 처리`() = runTest {
        // Given
        val accessToken = "expired_token"
        every {
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.AccessToken)
        } returns flowOf(accessToken)
        every { chain.proceed(mockRequestWithToken) } returns mockResponse
        every { mockResponse.isSuccessful } returns false
        every { mockResponse.code } returns 401
        every { mockResponse.request } returns mockRequest
        every { mockResponse.message } returns "Unauthorized"

        // When
        val result = tokenInterceptor.intercept(chain)

        // Then
        verify { mockRequestBuilder.addHeader("Authorization", "Bearer $accessToken") }
        assertEquals(mockResponse, result)
    }

    @Test
    fun `로깅이 정상적으로 호출되는지 확인`() = runTest {
        // Given
        val accessToken = "valid_token"
        every {
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.AccessToken)
        } returns flowOf(accessToken)
        every { chain.proceed(mockRequestWithToken) } returns mockResponse
        every { mockResponse.isSuccessful } returns true
        every { mockResponse.code } returns 200

        // When
        tokenInterceptor.intercept(chain)

        // Then
        verify { AppLogger.d(tag, "TokenInterceptor Triggered!") }
    }

    @Test
    fun `토큰이 있을 때 정확한 Authorization 헤더 형식 확인`() = runTest {
        // Given
        val accessToken = "test_access_token_123"
        every {
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.AccessToken)
        } returns flowOf(accessToken)
        every { chain.proceed(mockRequestWithToken) } returns mockResponse
        every { mockResponse.isSuccessful } returns true
        every { mockResponse.code } returns 200

        // When
        tokenInterceptor.intercept(chain)

        // Then
        verify {
            mockRequestBuilder.addHeader(
                RetrofitConstants.AUTHENTICATION,
                "${RetrofitConstants.BEARER} test_access_token_123",
            )
        }
    }
}
