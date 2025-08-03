package com.peekr.data.account.network

import com.peekr.data.account.model.request.LoginRequest
import com.peekr.data.account.model.response.LoginResponse
import com.peekr.data.shared.util.NetworkResult
import com.peekr.data.shared.util.network.NetworkErrorType
import com.peekr.domain.account.model.SocialLoginProvider
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class AccountNetworkDataSourceImplTest {
    private lateinit var server: MockWebServer
    private lateinit var moshi: Moshi
    private lateinit var accountApi: AccountApi
    private lateinit var dataSource: AccountNetworkDataSource

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

        dataSource = AccountNetworkDataSourceImpl(accountApi)
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
                setBody(JWT_TOKEN_BODY)
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
        dataSource = AccountNetworkDataSourceImpl(mockApi)
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
        dataSource = AccountNetworkDataSourceImpl(mockApi)
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

    companion object {
        private val mockLoginRequest = LoginRequest(SocialLoginProvider.GOOGLE, "123")
        private val mockAccessToken = "aaa.bbb.ccc"
        private val mockRefreshToken = "rrr.bbb.ccc"
        private val JWT_TOKEN_BODY =
            """
            {
                "accessToken": "$mockAccessToken",
                "refreshToken": "$mockRefreshToken"
            }
            """.trimIndent()
        private val mockLoginResponse = LoginResponse(mockAccessToken, mockRefreshToken)
    }
}
