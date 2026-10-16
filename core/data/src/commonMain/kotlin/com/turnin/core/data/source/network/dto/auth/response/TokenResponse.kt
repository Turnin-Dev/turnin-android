package com.turnin.core.data.source.network.dto.auth.response

import kotlinx.serialization.Serializable

/**
 * 토큰 갱신 응답 (Retrofit·Ktor 공용)
 *
 * @property accessToken 액세스 토큰
 * @property refreshToken 리프레시 토큰
 */
@Serializable
data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
)
