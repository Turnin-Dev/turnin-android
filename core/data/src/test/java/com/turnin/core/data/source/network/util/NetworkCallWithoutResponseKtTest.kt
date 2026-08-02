package com.turnin.core.data.source.network.util

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.turnin.core.data.MockLog
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

class NetworkCallWithoutResponseKtTest {
    private lateinit var apiService: ApiService
    private lateinit var server: MockWebServer
    private lateinit var moshi: Moshi

    @Before
    fun setUp() {
        MockLog.mock()

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
        try {
            server.shutdown()
        } finally {
            MockLog.cleanUp()
        }
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
}
