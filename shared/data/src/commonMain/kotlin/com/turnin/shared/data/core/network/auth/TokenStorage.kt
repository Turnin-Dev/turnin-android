package com.turnin.shared.data.core.network.auth

/**
 * 인증 토큰 저장소
 *
 * 공유 인증 로직(`installBearerAuth`)은 이 인터페이스만 알고, 구현은 플랫폼 보안 저장소를 사용한다.
 * - iOS: Keychain (`KeychainTokenStorage`)
 * - Android: 현재 미사용. Android는 기존 `DataStoreManager`(Keystore 암호화) + OkHttp `TokenAuthenticator`를 사용하며,
 *   공유 인증 로직을 쓰게 되면 `DataStoreManager`를 감싸는 구현을 추가한다.
 */
interface TokenStorage {
    /** 액세스 토큰 (없으면 null) */
    suspend fun getAccessToken(): String?

    /** 리프레시 토큰 (없으면 null) */
    suspend fun getRefreshToken(): String?

    /** 토큰 저장 */
    suspend fun saveTokens(accessToken: String, refreshToken: String)

    /** 토큰 삭제 */
    suspend fun clear()
}
