package com.peekr.core.data.auth.network

import com.peekr.core.data.ServerTestRule
import com.peekr.core.data.source.network.api.AuthApi
import com.peekr.core.data.source.network.datasource.AuthNetworkDataSource
import com.peekr.core.data.source.network.datasource.AuthNetworkDataSourceImpl
import com.peekr.core.data.source.network.dto.auth.request.ExistsUserRequest
import com.peekr.core.data.source.network.dto.auth.request.LoginRequest
import com.peekr.core.data.source.network.dto.auth.request.RegisterRequest
import com.peekr.core.data.source.network.dto.auth.response.LoginResponse
import com.peekr.core.data.source.network.dto.auth.response.RegisterResponse
import com.peekr.core.data.source.network.dto.file.response.PresignedUrlResponse
import com.peekr.core.data.source.network.error.NetworkErrorType
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.SocialLoginProvider
import com.peekr.core.domain.model.UserId
import com.squareup.moshi.JsonDataException
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AuthNetworkDataSourceImplTest {
    @get:Rule
    val testRule = ServerTestRule()
    private val authApi: AuthApi
        get() = testRule.createNetworkApi<AuthApi>(testRule.moshi)
    private lateinit var dataSource: AuthNetworkDataSource
    private lateinit var testOkHttpClient: OkHttpClient

    @Before
    fun setUp() {
        testOkHttpClient = OkHttpClient.Builder().build()
        dataSource = AuthNetworkDataSourceImpl(authApi)
    }

    @Test
    fun `login() 성공 테스트`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(mockLoginResponseJson)
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
        testRule.server.enqueue(
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
        val mockApi: AuthApi = mockk()
        val exception = IllegalStateException()
        dataSource = AuthNetworkDataSourceImpl(mockApi)
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
        val mockApi: AuthApi = mockk()
        dataSource = AuthNetworkDataSourceImpl(mockApi)
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
        testRule.server.enqueue(
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
        testRule.server.enqueue(
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
        val mockApi: AuthApi = mockk()
        dataSource = AuthNetworkDataSourceImpl(mockApi)
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
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(mockExistsResponseJson)
            },
        )

        // when
        val result = dataSource.existsDisplayId(mockDisplayId)

        // then
        assertTrue(result is NetworkResult.Success)
        assertTrue((result as NetworkResult.Success).data.exists)
    }

    @Test
    fun `existsDisplayId() 실패 테스트 (정의된 API 예외 발생)`() = runTest {
        // given
        val mockApi: AuthApi = mockk()
        dataSource = AuthNetworkDataSourceImpl(mockApi)
        coEvery { mockApi.existsDisplayId(any()) } throws JsonDataException("smile")

        // when
        val result = dataSource.existsDisplayId(mockDisplayId)

        // then
        assertTrue(result is NetworkResult.Error)
        assertEquals((result as NetworkResult.Error).error, NetworkErrorType.Exception.JsonData)
    }

    @Test
    fun `register() 성공 테스트`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(mockRegisterResponseJson)
            },
        )

        // when
        val result = dataSource.register(mockRegisterRequest)

        // then
        assertTrue(result is NetworkResult.Success)
        assertEquals(mockRegisterResponse, (result as NetworkResult.Success).data)
    }

    @Test
    fun `register() 실패 테스트 (정의된 API 예외 발생)`() = runTest {
        // given
        val mockApi: AuthApi = mockk()
        dataSource = AuthNetworkDataSourceImpl(mockApi)
        coEvery { mockApi.register(any()) } throws JsonDataException("smile")

        // when
        val result = dataSource.register(mockRegisterRequest)

        // then
        assertTrue(result is NetworkResult.Error)
        assertEquals((result as NetworkResult.Error).error, NetworkErrorType.Exception.JsonData)
    }

    companion object {
        private val mockUserId = UserId(1L)
        private val mockLoginRequest = LoginRequest(SocialLoginProvider.GOOGLE, "123")
        private const val MOCK_ACCESS_TOKEN = "aaa.bbb.ccc"
        private const val MOCK_REFRESH_TOKEN = "rrr.bbb.ccc"
        private val mockLoginResponse =
            LoginResponse(mockUserId.value, MOCK_ACCESS_TOKEN, MOCK_REFRESH_TOKEN)
        private val mockExistsUserRequest = ExistsUserRequest(SocialLoginProvider.GOOGLE, "123")
        private val mockExistsResponseJson =
            """
            {
                "exists": true
            }
            """.trimIndent()
        private val mockDisplayId = DisplayId("123")
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
        private val mockRegisterRequest =
            RegisterRequest(SocialLoginProvider.GOOGLE, "asd", "asd", "asd", null, null)
        private val mockRegisterResponse =
            RegisterResponse(mockUserId.value, MOCK_ACCESS_TOKEN, MOCK_REFRESH_TOKEN)
        private val mockLoginResponseJson =
            """
            {
              "userId": ${mockUserId.value},
              "accessToken": "$MOCK_ACCESS_TOKEN",
              "refreshToken": "$MOCK_REFRESH_TOKEN"
            }
            """.trimIndent()
        private val mockRegisterResponseJson =
            """
            {
              "userId": ${mockUserId.value},
              "accessToken": "$MOCK_ACCESS_TOKEN",
              "refreshToken": "$MOCK_REFRESH_TOKEN"
            }
            """.trimIndent()
    }
}
