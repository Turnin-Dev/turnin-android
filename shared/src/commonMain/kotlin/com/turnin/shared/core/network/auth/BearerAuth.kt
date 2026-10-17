package com.turnin.shared.core.network.auth

import com.turnin.shared.core.network.ApiPath
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.AuthCircuitBreaker
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException
import kotlinx.io.IOException

private const val BEARER = "Bearer"

/**
 * Ktor bearer 인증을 설치한다. (Android `TokenInterceptor` / `TokenAuthenticator`와 같은 규칙)
 *
 * - 저장된 액세스 토큰이 있으면 모든 요청에 첨부한다.
 * - 401 응답 시 리프레시 토큰으로 갱신 후 원래 요청을 재시도한다. (동시 요청의 갱신은 Ktor가 한 번만 수행)
 * - 로그아웃 요청의 401은 갱신하지 않는다.
 * - 네트워크(IO) 오류로 갱신하지 못하면 토큰을 유지한다.
 * - 그 외 갱신 실패 시 토큰을 삭제하고 [onAuthFailure]를 호출한다. (자동 로그아웃)
 *
 * 토큰을 외부에서 바꾼 경우 `HttpClient.authProvider<BearerAuthProvider>()?.clearToken()`으로 캐시를 비워야 한다.
 *
 * @param tokenStorage 토큰 저장소
 * @param onAuthFailure 인증 실패(자동 로그아웃) 시 호출
 */
fun HttpClientConfig<*>.installBearerAuth(
    tokenStorage: TokenStorage,
    onAuthFailure: suspend () -> Unit,
) {
    install(Auth) {
        bearer {
            // 갱신 요청은 제외한다: Ktor bearer는 토큰을 붙이기 전에 기존 Authorization 헤더를 지우므로,
            // 갱신 요청까지 포함하면 직접 넣은 리프레시 토큰 헤더가 사라진다.
            sendWithoutRequest { request -> !request.attributes.contains(AuthCircuitBreaker) }

            loadTokens {
                val accessToken = tokenStorage.getAccessToken()?.takeIf { it.isNotEmpty() } ?: return@loadTokens null
                BearerTokens(accessToken, tokenStorage.getRefreshToken())
            }

            refreshTokens {
                if (response.call.request.url.encodedPath.contains(ApiPath.User.LOGOUT)) {
                    return@refreshTokens null
                }

                val refreshToken = tokenStorage.getRefreshToken() ?: run {
                    tokenStorage.clearAndNotify(onAuthFailure)
                    return@refreshTokens null
                }

                try {
                    val refreshResponse = client.get(ApiPath.Auth.REFRESH) {
                        markAsRefreshTokenRequest()
                        header(HttpHeaders.Authorization, refreshToken.withBearerPrefix())
                    }
                    if (!refreshResponse.status.isSuccess()) {
                        tokenStorage.clearAndNotify(onAuthFailure)
                        return@refreshTokens null
                    }

                    val tokens = refreshResponse.body<TokenResponse>()
                    tokenStorage.saveTokens(tokens.accessToken, tokens.refreshToken)
                    BearerTokens(tokens.accessToken, tokens.refreshToken)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: IOException) {
                    // 네트워크 문제는 일시적일 수 있으므로 토큰을 유지하고 원래 401 응답을 반환한다.
                    null
                } catch (e: Exception) {
                    tokenStorage.clearAndNotify(onAuthFailure)
                    null
                }
            }
        }
    }
}

/** 토큰을 삭제하고 인증 실패(자동 로그아웃)를 알린다. */
private suspend fun TokenStorage.clearAndNotify(onAuthFailure: suspend () -> Unit) {
    clear()
    onAuthFailure()
}

private fun String.withBearerPrefix(): String =
    if (trimStart().startsWith("$BEARER ", ignoreCase = true)) this else "$BEARER $this"
