package com.turnin.core.domain.auth.model

/**
 * JWT 형식의 토큰 (액세스 토큰, 리프레쉬 토큰 포함)
 *
 * @property accessToken 액세스 토큰
 * @property refreshToken 리프레쉬 토큰
 */
data class JWTToken(
    val accessToken: String,
    val refreshToken: String,
)
