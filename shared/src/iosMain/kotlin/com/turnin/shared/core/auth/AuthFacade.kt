package com.turnin.shared.core.auth

import com.turnin.shared.data.core.network.auth.TokenStorage
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.Flow

/**
 * Swift에서 사용하는 인증 API
 *
 * 로그인 기능 전까지는 개발용 토큰 입력([saveTokens])에 사용한다.
 */
class AuthFacade internal constructor(
    private val tokenStorage: TokenStorage,
    private val authHttpClient: HttpClient,
    private val authEvents: AuthEvents,
) {
    /**
     * 토큰 갱신 실패(자동 로그아웃) 이벤트
     *
     * 구독 중일 때만 전달되므로 Swift에서는 앱 시작 시점부터 구독한다.
     */
    val authFailures: Flow<Unit> get() = authEvents.authFailures

    /**
     * 토큰을 저장하고, 이미 불러온 토큰 캐시를 비워 다음 요청부터 새 토큰을 사용한다.
     *
     * @throws IllegalStateException Keychain 저장 실패 시 (Swift에서는 `throws`)
     */
    @Throws(IllegalStateException::class, CancellationException::class)
    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        tokenStorage.saveTokens(accessToken, refreshToken)
        clearCachedTokens()
    }

    /** 저장된 토큰을 삭제한다. */
    suspend fun clearTokens() {
        tokenStorage.clear()
        clearCachedTokens()
    }

    /** 저장된 액세스 토큰이 있는지 확인한다. */
    suspend fun hasTokens(): Boolean = !tokenStorage.getAccessToken().isNullOrEmpty()

    private fun clearCachedTokens() {
        authHttpClient.authProvider<BearerAuthProvider>()?.clearToken()
    }
}
