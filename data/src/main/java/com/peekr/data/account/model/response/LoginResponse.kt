package com.peekr.data.account.model.response

/**
 * 로그인 응답 바디
 *
 * @property accessToken JWT 형식의 액세스 토큰
 * @property refreshToken JWT 형식의 리프레쉬 토큰
 */
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
)
