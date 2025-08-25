package com.peekr.data.account.network

import com.peekr.data.account.model.request.DisplayIdRequest
import com.peekr.data.account.model.request.ExistsUserRequest
import com.peekr.data.account.model.request.LoginRequest
import com.peekr.data.account.model.response.LoginResponse
import com.peekr.data.account.model.response.PresignedUrlResponse
import com.peekr.data.shared.util.network.NetworkErrorType
import com.peekr.data.shared.util.network.NetworkResult
import com.peekr.domain.account.model.SocialLoginProvider
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@OptIn(ExperimentalCoroutinesApi::class)
class AccountNetworkDataSourceImplTest {
    private lateinit var server: MockWebServer
    private lateinit var moshi: Moshi
    private lateinit var accountApi: AccountApi
    private lateinit var dataSource: AccountNetworkDataSource
    private lateinit var testOkHttpClient: OkHttpClient

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()

        moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

        accountApi =
            Retrofit
                .Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .baseUrl(server.url("/"))
                .build()
                .create(AccountApi::class.java)

        testOkHttpClient = OkHttpClient.Builder().build()

        dataSource = AccountNetworkDataSourceImpl(accountApi, testOkHttpClient)
    }

    @After
    fun teardown() {
        server.shutdown()
    }

    @Test
    fun `login() 성공 테스트`() = runTest {
        // given
        server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(MOCK_JWT_TOKEN_BODY)
            },
        )

        // when
        val result = dataSource.login(mockLoginRequest)

        // then
        assertTrue(result is NetworkResult.Success)
        assertEquals((result as NetworkResult.Success).data, mockLoginResponse)
    }

    @Test
    fun `login() 실패 테스트 (잘못된 응답 바디)`() = runTest {
        // given
        server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(
                    """
                    {
                        "wrong":"hello"
                    }
                    """.trimIndent(),
                )
            },
        )

        // when
        val result = dataSource.login(mockLoginRequest)

        // then
        assertTrue(result is NetworkResult.Error)
        assertEquals((result as NetworkResult.Error).error, NetworkErrorType.Exception.JsonData)
    }

    @Test
    fun `login() 실패 테스트 (알 수 없는 API 예외 발생)`() = runTest {
        // given
        val mockApi: AccountApi = mockk()
        val exception = IllegalStateException()
        dataSource = AccountNetworkDataSourceImpl(mockApi, testOkHttpClient)
        coEvery { mockApi.login(any()) } throws exception

        // when
        val result = dataSource.login(mockLoginRequest)

        // then
        assertTrue(result is NetworkResult.Error)
        assertEquals((result as NetworkResult.Error).error, NetworkErrorType.Unexpected(exception))
    }

    @Test
    fun `login() 실패 테스트 (정의된 API 예외 발생)`() = runTest {
        // given
        val mockApi: AccountApi = mockk()
        dataSource = AccountNetworkDataSourceImpl(mockApi, testOkHttpClient)
        coEvery { mockApi.login(any()) } throws JsonDataException("smile")

        // when
        val result = dataSource.login(mockLoginRequest)

        // then
        assertTrue(result is NetworkResult.Error)
        assertEquals((result as NetworkResult.Error).error, NetworkErrorType.Exception.JsonData)
    }

    @Test
    fun `login() 실패 테스트 (재시도 불가능한 HTTP 상태코드(4xx) 응답)`() = runTest {
        // given
        server.enqueue(
            MockResponse().apply {
                setResponseCode(409)
            },
        )

        // when
        val result = dataSource.login(mockLoginRequest)

        // then
        assertTrue(result is NetworkResult.Error)
        assertEquals((result as NetworkResult.Error).error, NetworkErrorType.Network.Conflict)
    }

    @Test
    fun `existsUser() 성공 테스트`() = runTest {
        // given
        server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(mockExistsResponseJson)
            },
        )

        // when
        val result = dataSource.existsUser(mockExistsUserRequest)

        // then
        assertTrue(result is NetworkResult.Success)
        assertTrue((result as NetworkResult.Success).data.exists)
    }

    @Test
    fun `existsUser() 실패 테스트 (정의된 API 예외 발생)`() = runTest {
        // given
        val mockApi: AccountApi = mockk()
        dataSource = AccountNetworkDataSourceImpl(mockApi, testOkHttpClient)
        coEvery { mockApi.existsUser(any(), any()) } throws JsonDataException("smile")

        // when
        val result = dataSource.existsUser(mockExistsUserRequest)

        // then
        assertTrue(result is NetworkResult.Error)
        assertEquals((result as NetworkResult.Error).error, NetworkErrorType.Exception.JsonData)
    }

    @Test
    fun `existsDisplayId() 성공 테스트`() = runTest {
        // given
        server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(mockExistsResponseJson)
            },
        )

        // when
        val result = dataSource.existsDisplayId(mockDisplayIdRequest)

        // then
        assertTrue(result is NetworkResult.Success)
        assertTrue((result as NetworkResult.Success).data.exists)
    }

    @Test
    fun `existsDisplayId() 실패 테스트 (정의된 API 예외 발생)`() = runTest {
        // given
        val mockApi: AccountApi = mockk()
        dataSource = AccountNetworkDataSourceImpl(mockApi, testOkHttpClient)
        coEvery { mockApi.existsDisplayId(any()) } throws JsonDataException("smile")

        // when
        val result = dataSource.existsDisplayId(mockDisplayIdRequest)

        // then
        assertTrue(result is NetworkResult.Error)
        assertEquals((result as NetworkResult.Error).error, NetworkErrorType.Exception.JsonData)
    }

    @Test
    fun `getFileUploadPresignedUrl() 성공 테스트`() = runTest {
        // given
        server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(mockPresignedResponseJson)
            },
        )

        // when
        val result = dataSource.getFileUploadPresignedUrl("my-image.jpg", "image/jpeg")

        // then
        assertTrue(result is NetworkResult.Success)
        assertEquals(
            mockPresignUrlResponse,
            (result as NetworkResult.Success).data,
        )
    }

    @Test
    fun `getFileUploadPresignedUrl() 실패 테스트 (정의된 API 예외 발생)`() = runTest {
        // given
        val mockApi: AccountApi = mockk()
        dataSource = AccountNetworkDataSourceImpl(mockApi, testOkHttpClient)
        coEvery { mockApi.getFileUploadPresignedUrl(any(), any()) } throws JsonDataException("smile")

        // when
        val result = dataSource.getFileUploadPresignedUrl("asd", "asd")

        // then
        assertTrue(result is NetworkResult.Error)
        assertEquals((result as NetworkResult.Error).error, NetworkErrorType.Exception.JsonData)
    }

    companion object {
        private val mockLoginRequest = LoginRequest(SocialLoginProvider.GOOGLE, "123")
        private const val MOCK_ACCESS_TOKEN = "aaa.bbb.ccc"
        private const val MOCK_REFRESH_TOKEN = "rrr.bbb.ccc"
        private val MOCK_JWT_TOKEN_BODY =
            """
            {
                "accessToken": "$MOCK_ACCESS_TOKEN",
                "refreshToken": "$MOCK_REFRESH_TOKEN"
            }
            """.trimIndent()
        private val mockLoginResponse = LoginResponse(MOCK_ACCESS_TOKEN, MOCK_REFRESH_TOKEN)
        private val mockExistsUserRequest = ExistsUserRequest(SocialLoginProvider.GOOGLE, "123")
        private val mockExistsResponseJson =
            """
            {
                "exists": true
            }
            """.trimIndent()
        private val mockDisplayIdRequest = DisplayIdRequest("123")
        private const val MOCK_PRESIGNED_URL = "https://example-storage.com/objects/my-image.jpg"
        private const val MOCK_METHOD = "PUT"
        private const val MOCK_SECONDS = 600
        private val mockPresignedResponseJson =
            """
            {
              "presignedUrl": "$MOCK_PRESIGNED_URL",
              "method": "$MOCK_METHOD",
              "expiresInSeconds": $MOCK_SECONDS
            }
            """.trimIndent()
        private val mockPresignUrlResponse = PresignedUrlResponse(
            MOCK_PRESIGNED_URL,
            MOCK_METHOD,
            MOCK_SECONDS,
        )
    }
}
