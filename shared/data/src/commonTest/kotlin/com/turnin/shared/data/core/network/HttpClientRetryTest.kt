package com.turnin.shared.data.core.network

import com.turnin.shared.data.core.network.policy.NetworkPolicy
import com.turnin.shared.domain.core.error.NetworkErrorType
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class HttpClientRetryTest {
    private val delays = mutableListOf<Long>()
    private var requestCount = 0

    private fun clientResponding(vararg statuses: HttpStatusCode): HttpClient {
        val engine = MockEngine {
            val status = statuses[requestCount.coerceAtMost(statuses.lastIndex)]
            requestCount++
            respond(content = "{}", status = status)
        }
        return createHttpClient(engine, BASE_URL, retryDelay = { delays += it })
    }

    @Test
    fun `재시도 가능한 상태 코드는 모든 재시도 전에 지수 백오프로 대기한다`() = runTest {
        val client = clientResponding(HttpStatusCode.ServiceUnavailable, HttpStatusCode.ServiceUnavailable, HttpStatusCode.OK)

        val result = ktorNetworkCallWithoutResponse { client.get("/test") }

        assertTrue(result is NetworkResult.Success)
        assertEquals(3, requestCount)
        assertEquals(2, delays.size)
        assertTrue(delays[0] in 250L until 500L, "first delay=${delays[0]}")
        assertTrue(delays[1] in 500L until 1_000L, "second delay=${delays[1]}")
    }

    @Test
    fun `재시도를 모두 소진하면 마지막 응답의 상태 코드 에러로 반환한다`() = runTest {
        val client = clientResponding(HttpStatusCode.ServiceUnavailable)

        val result = ktorNetworkCallWithoutResponse { client.get("/test") }

        assertEquals(NetworkErrorType.Network.ServiceUnavailable, (result as NetworkResult.Error).error)
        assertEquals(NetworkPolicy.Retry.MAX_RETRIES + 1, requestCount)
        assertEquals(NetworkPolicy.Retry.MAX_RETRIES, delays.size)
    }

    @Test
    fun `재시도 불가능한 상태 코드는 재시도하지 않는다`() = runTest {
        val client = clientResponding(HttpStatusCode.Unauthorized)

        val result = ktorNetworkCallWithoutResponse { client.get("/test") }

        assertEquals(NetworkErrorType.Network.Unauthorized, (result as NetworkResult.Error).error)
        assertEquals(1, requestCount)
        assertTrue(delays.isEmpty())
    }

    companion object {
        private const val BASE_URL = "https://turnin.test"
    }
}
