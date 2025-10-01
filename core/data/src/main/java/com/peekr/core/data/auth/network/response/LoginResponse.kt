package com.peekr.core.data.auth.network.response

import com.peekr.core.domain.auth.model.JWTToken
import com.squareup.moshi.JsonClass

/**
 * 로그인 응답 바디
 *
 * @property accessToken JWT 형식의 액세스 토큰
 * @property refreshToken JWT 형식의 리프레쉬 토큰
 */
@JsonClass(generateAdapter = true)
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
)

fun LoginResponse.toDomainModel(): JWTToken = JWTToken(
    accessToken = accessToken,
    refreshToken = refreshToken,
)
