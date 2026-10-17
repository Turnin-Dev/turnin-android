package com.turnin.shared.data.core.network.auth

import kotlinx.serialization.Serializable

/**
 * 토큰 갱신 응답
 *
 * @property accessToken 액세스 토큰
 * @property refreshToken 리프레시 토큰
 */
@Serializable
internal data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
)
