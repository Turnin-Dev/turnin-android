package com.peekr.core.domain.auth.model

import com.peekr.core.domain.model.UserId

/**
 * 회원가입 결과
 *
 * @property userId 사용자 ID
 * @property accessToken JWT 액세스 토큰
 * @property refreshToken JWT 리프레쉬 토큰
 */
data class RegisterResult(
    val userId: UserId,
    val accessToken: String,
    val refreshToken: String,
)
