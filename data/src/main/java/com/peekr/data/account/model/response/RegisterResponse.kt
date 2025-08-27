package com.peekr.data.account.model.response

import com.peekr.domain.account.model.JWTToken
import com.squareup.moshi.JsonClass

/**
 * 회원가입 응답 바디
 *
 * @property accessToken JWT 형식의 액세스 토큰
 * @property refreshToken JWT 형식의 리프레쉬 토큰
 */
@JsonClass(generateAdapter = true)
data class RegisterResponse(
    val accessToken: String,
    val refreshToken: String,
)

fun RegisterResponse.toDomainModel(): JWTToken = JWTToken(
    accessToken = accessToken,
    refreshToken = refreshToken,
)
