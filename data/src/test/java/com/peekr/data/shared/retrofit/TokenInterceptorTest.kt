package com.peekr.data.shared.retrofit

import com.peekr.domain.shared.dataStore.DataStoreKey
import com.peekr.domain.shared.dataStore.DataStoreManager
import io.mockk.clearAllMocks
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import timber.log.Timber

@RunWith(RobolectricTestRunner::class)
class TokenInterceptorTest {
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

        // 기본적인 chain.proceed mock 설정
        every { chain.proceed(any()) } returns mockResponse

        // Timber 로깅 mock
        val mockTree = mockk<Timber.Tree>(relaxed = true)
        Timber.plant(mockTree)
    }

    @After
    fun tearDown() {
        clearAllMocks()
        unmockkStatic(Timber::class)
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
        verify { Timber.d("Response is Successful (HTTP status code is 200 OK)") }
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
    fun `DataStore에서 예외 발생 시 catch로 null 처리하여 원본 요청 진행`() = runTest {
        // Given
        every {
            dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.AccessToken)
        } returns flow<String?> {
            throw RuntimeException("DataStore error")
        }

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
        verify { Timber.d("Response is Successful (HTTP status code is 200 OK)") }
    }

    @Test
    fun `intercept - 응답이 성공(201)인 경우 성공 로그 출력`() = runTest {
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
        verify { Timber.d("Response is Successful (HTTP status code is 201 Created)") }
    }

    @Test
    fun `intercept - 응답이 실패(404)인 경우 실패 로그 출력`() = runTest {
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
        verify { Timber.d("Response is Failure (HTTP status code is 404 Not Found)") }
        verify { Timber.d("request: $mockRequest\nmessage: Not Found") }
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
        verify { Timber.d("Response is Successful (HTTP status code is 202)") }
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
        verify { Timber.d("Response is Failure (HTTP status code is 500)") }
        verify { Timber.d("request: $mockRequest\nmessage: Internal Server Error") }
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
        verify { Timber.d("Response is Failure (HTTP status code is 401)") }
        verify { Timber.d("request: $mockRequest\nmessage: Unauthorized") }
        assertEquals(mockResponse, result)
    }

    @Test
    fun `Timber 로깅이 정상적으로 호출되는지 확인`() = runTest {
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
        verify { Timber.d("TokenInterceptor Triggered!") }
        verify { Timber.d("Response is Successful (HTTP status code is 200 OK)") }
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
        verify { mockRequestBuilder.addHeader("Authorization", "Bearer test_access_token_123") }
    }

    @Test
    fun `여러 다른 HTTP 상태 코드들에 대한 로깅 확인`() = runTest {
        val testCases = listOf(
            Triple(403, false, "Response is Failure (HTTP status code is 403)"),
            Triple(400, false, "Response is Failure (HTTP status code is 400)"),
            Triple(202, true, "Response is Successful (HTTP status code is 202)"),
            Triple(204, true, "Response is Successful (HTTP status code is 204)"),
        )

        testCases.forEach { (statusCode, isSuccess, expectedLog) ->
            // Given
            clearMocks(dataStoreManager, chain, mockResponse)
            every { dataStoreManager.getEncryptedStringData(DataStoreKey.Auth.AccessToken) } returns flowOf("token")
            every { chain.request() } returns mockRequest
            every { mockRequest.newBuilder() } returns mockRequestBuilder
            every { mockRequestBuilder.addHeader(any(), any()) } returns mockRequestBuilder
            every { mockRequestBuilder.build() } returns mockRequestWithToken
            every { chain.proceed(mockRequestWithToken) } returns mockResponse
            every { mockResponse.isSuccessful } returns isSuccess
            every { mockResponse.code } returns statusCode
            if (!isSuccess) {
                every { mockResponse.request } returns mockRequest
                every { mockResponse.message } returns "Error message"
            }

            // When
            tokenInterceptor.intercept(chain)

            // Then
            verify { Timber.d(expectedLog) }
        }
    }
}
