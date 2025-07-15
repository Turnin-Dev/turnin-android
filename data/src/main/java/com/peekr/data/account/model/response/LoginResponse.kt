package com.peekr.data.account.model.response

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
