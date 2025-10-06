package com.peekr.core.data.network.util

import com.ibm.icu.util.TimeUnit
import com.peekr.core.data.network.CommonErrorResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@ExperimentalCoroutinesApi
class NetworkCallTest {
    private lateinit var apiService: ApiService
    private lateinit var server: MockWebServer
    private lateinit var moshi: Moshi

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()

        moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

        val client = OkHttpClient
            .Builder()
            .readTimeout(1000, java.util.concurrent.TimeUnit.MICROSECONDS)
            .connectTimeout(1000, java.util.concurrent.TimeUnit.MICROSECONDS)
            .writeTimeout(1000, java.util.concurrent.TimeUnit.MICROSECONDS)
            .build()

        apiService =
            Retrofit
                .Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .baseUrl(server.url("/"))
                .client(client)
                .build()
                .create(ApiService::class.java)
    }

    @After
    fun teardown() {
        server.shutdown()
    }

    @Test
    fun `networkCall 성공 테스트`() = runTest {
        // given
        val jsonTestData =
            """
            {
                "message": "테스트"
            }
            """.trimIndent()
        val adapter = moshi.adapter(TestModel::class.java)
        val expectedResponse = adapter.fromJson(jsonTestData)

        server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(jsonTestData)
            },
        )

        // when
        val result = networkCall { apiService.testCall() }

        // then
        assertTrue(result is NetworkResult.Success)
        assertNotNull(expectedResponse)
        assertEquals(
            expectedResponse,
            (result as NetworkResult.Success).data,
        )
    }

    @Test
    fun `networkCall 에러 테스트 (HTTP 상태 코드)`() = runTest {
        // given
        val expectedCode = 404
        server.enqueue(MockResponse().apply { setResponseCode(expectedCode) })

        // when
        val result = networkCall { apiService.testCall() }

        // then
        assertTrue(result is NetworkResult.Error)
        assertEquals(expectedCode, (result as NetworkResult.Error).status)
    }

    @Test
    fun `networkCall 에러 테스트 (에러 메시지)`() = runTest {
        // given
        val expectedCode = 404
        server.enqueue(
            MockResponse().apply {
                setResponseCode(expectedCode)
                setBody(ERROR_RESPONSE)
            },
        )

        // when
        val result = networkCall { apiService.testCall() }

        // then
        assertTrue(result is NetworkResult.Error)
        assertEquals(expectedCode, (result as NetworkResult.Error).status)
        assertEquals(errorResponse.message, result.message)
    }

    @Test
    fun `networkCall 타임아웃 예외 테스트`() = runTest {
        // given
        server.enqueue(MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE))

        // when
        val result = networkCall { apiService.testCall() }

        // then
        assertTrue(result is NetworkResult.Error)
        assertEquals(NetworkErrorType.Exception.TimeOut, (result as NetworkResult.Error).error)
    }

    @Test
    fun `networkCall JSON 파싱 에러 테스트`() = runTest {
        // given
        server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody("""{ "code": "should-be-int", "messaage": 878787 }""")
            },
        )
        server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody("""{ "mesjlkjlksage": "success" }""")
            },
        )
        server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody("""{ "mesjlkjlksage": "success" }""")
            },
        )

        // when
        val result = networkCall { apiService.testCall() }

        // then
        assertTrue(result is NetworkResult.Error)
        assertEquals(NetworkErrorType.Exception.JsonData, (result as NetworkResult.Error).error)
    }

    @Test
    fun `networkCall 예외 발생 시 재시도 동작 확인`() = runTest {
        // given
        val jsonTestData =
            """
            {
                "message": "테스트"
            }
            """.trimIndent()
        server.enqueue(MockResponse().setResponseCode(500))
        server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(jsonTestData)
            },
        )

        // when
        val result = networkCall { apiService.testCall() }

        // then
        assertTrue(result is NetworkResult.Success)
        assertEquals(2, server.requestCount)
    }

    companion object {
        private const val ERROR_CODE = "A001"
        private const val ERROR_MESSAGE = "Login failed"
        private const val STATUS = 400
        private val ERROR_RESPONSE =
            """
            {
              "code": "$ERROR_CODE",
              "message": "$ERROR_MESSAGE",
              "status": $STATUS
            }
            """.trimIndent()
        private val errorResponse = CommonErrorResponse(
            code = ERROR_CODE,
            message = ERROR_MESSAGE,
            status = STATUS,
        )
    }
}
