package com.turnin.shared.data.core.network.auth

import com.turnin.shared.data.core.network.ApiPath
import com.turnin.shared.data.core.network.NetworkResult
import com.turnin.shared.data.core.network.createHttpClient
import com.turnin.shared.data.core.network.ktorNetworkCallWithoutResponse
import com.turnin.shared.domain.core.error.NetworkErrorType
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class BearerAuthTest {
    private val tokenStorage = FakeTokenStorage()
    private val requests = mutableListOf<HttpRequestData>()
    private var authFailureCount = 0

    private fun client(handler: MockRequestHandleScope.(HttpRequestData) -> HttpResponseData): HttpClient {
        val engine = MockEngine { request ->
            requests += request
            handler(request)
        }
        return createHttpClient(engine, BASE_URL, retryDelay = {}) {
            installBearerAuth(tokenStorage, onAuthFailure = { authFailureCount++ })
        }
    }

    @Test
    fun `저장된 액세스 토큰을 요청에 첨부한다`() = runTest {
        tokenStorage.saveTokens(ACCESS, REFRESH)
        val client = client { respond("{}") }

        ktorNetworkCallWithoutResponse { client.get(PATH) }

        assertEquals("Bearer $ACCESS", requests.single().headers[HttpHeaders.Authorization])
    }

    @Test
    fun `토큰이 없으면 Authorization 헤더 없이 요청한다`() = runTest {
        val client = client { respond("{}") }

        ktorNetworkCallWithoutResponse { client.get(PATH) }

        assertNull(requests.single().headers[HttpHeaders.Authorization])
    }

    @Test
    fun `401 응답 시 리프레시 토큰으로 갱신 후 새 토큰으로 재시도한다`() = runTest {
        tokenStorage.saveTokens(ACCESS, REFRESH)
        val client = client { request ->
            when {
                request.url.encodedPath == ApiPath.Auth.REFRESH -> respondJson(
                    """{"accessToken":"$NEW_ACCESS","refreshToken":"$NEW_REFRESH"}""",
                )
                request.headers[HttpHeaders.Authorization] == "Bearer $NEW_ACCESS" -> respond("{}")
                else -> respond("", HttpStatusCode.Unauthorized)
            }
        }

        val result = ktorNetworkCallWithoutResponse { client.get(PATH) }

        assertTrue(result is NetworkResult.Success)
        val refreshRequest = requests.single { it.url.encodedPath == ApiPath.Auth.REFRESH }
        assertEquals("Bearer $REFRESH", refreshRequest.headers[HttpHeaders.Authorization])
        assertEquals(NEW_ACCESS, tokenStorage.getAccessToken())
        assertEquals(NEW_REFRESH, tokenStorage.getRefreshToken())
        assertEquals(0, authFailureCount)
    }

    @Test
    fun `토큰 갱신에 실패하면 토큰을 삭제하고 인증 실패를 알린다`() = runTest {
        tokenStorage.saveTokens(ACCESS, REFRESH)
        val client = client { respond("", HttpStatusCode.Unauthorized) }

        val result = ktorNetworkCallWithoutResponse { client.get(PATH) }

        assertEquals(NetworkErrorType.Network.Unauthorized, (result as NetworkResult.Error).error)
        assertNull(tokenStorage.getAccessToken())
        assertNull(tokenStorage.getRefreshToken())
        assertEquals(1, authFailureCount)
    }

    @Test
    fun `로그아웃 요청의 401은 토큰을 갱신하지 않는다`() = runTest {
        tokenStorage.saveTokens(ACCESS, REFRESH)
        val client = client { respond("", HttpStatusCode.Unauthorized) }

        ktorNetworkCallWithoutResponse { client.post(ApiPath.User.LOGOUT) }

        assertTrue(requests.none { it.url.encodedPath == ApiPath.Auth.REFRESH })
        assertEquals(ACCESS, tokenStorage.getAccessToken())
        assertEquals(0, authFailureCount)
    }

    private fun MockRequestHandleScope.respondJson(content: String): HttpResponseData =
        respond(content, headers = headersOf(HttpHeaders.ContentType, "application/json"))

    private class FakeTokenStorage : TokenStorage {
        private var accessToken: String? = null
        private var refreshToken: String? = null

        override suspend fun getAccessToken(): String? = accessToken

        override suspend fun getRefreshToken(): String? = refreshToken

        override suspend fun saveTokens(accessToken: String, refreshToken: String) {
            this.accessToken = accessToken
            this.refreshToken = refreshToken
        }

        override suspend fun clear() {
            accessToken = null
            refreshToken = null
        }
    }

    companion object {
        private const val BASE_URL = "https://turnin.test"
        private const val PATH = "/announcement"
        private const val ACCESS = "access"
        private const val REFRESH = "refresh"
        private const val NEW_ACCESS = "new-access"
        private const val NEW_REFRESH = "new-refresh"
    }
}
