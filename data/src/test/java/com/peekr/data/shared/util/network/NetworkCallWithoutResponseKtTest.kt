package com.peekr.data.shared.util.network

import com.peekr.data.shared.util.NetworkResult
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class NetworkCallWithoutResponseKtTest {
    private lateinit var apiService: ApiService
    private lateinit var server: MockWebServer
    private lateinit var moshi: Moshi

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()

        moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

        apiService =
            Retrofit
                .Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .baseUrl(server.url("/"))
                .build()
                .create(ApiService::class.java)
    }

    @After
    fun teardown() {
        server.shutdown()
    }

    @Test
    fun `networkCallWithoutResponse 성공 테스트`() = runTest {
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
        val result = networkCallWithoutResponse { apiService.testCall() }

        // then
        assertTrue(result is NetworkResult.Success)
        assertNotNull(expectedResponse)
    }

    @Test
    fun `networkCallWithoutResponse 에러 테스트`() = runTest {
        // given
        val expectedCode = 404
        server.enqueue(MockResponse().apply { setResponseCode(expectedCode) })

        // when
        val result = networkCallWithoutResponse { apiService.testCall() }

        // then
        assertTrue(result is NetworkResult.Error)
        assertEquals(expectedCode, (result as NetworkResult.Error).status)
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
    }
}
