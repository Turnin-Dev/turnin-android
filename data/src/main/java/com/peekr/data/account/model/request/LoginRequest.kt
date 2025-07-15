package com.peekr.data.account.model.request

/**
 * 로그인 요청 바디
 *
 * @property provider 소셜로그인 플랫폼
 * @property providerId 소셜로그인 플랫폼에서 제공된 id
 */
data class LoginRequest(
    val provider: String,
    val providerId: String,
)
