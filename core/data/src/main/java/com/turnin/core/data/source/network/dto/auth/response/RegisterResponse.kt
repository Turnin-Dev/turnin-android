package com.turnin.core.data.source.network.dto.auth.response

import com.squareup.moshi.JsonClass
import com.turnin.core.domain.auth.model.RegisterResult
import com.turnin.core.domain.model.UserId

/**
 * 회원가입 응답 바디
 *
 * @property accessToken JWT 형식의 액세스 토큰
 * @property refreshToken JWT 형식의 리프레쉬 토큰
 */
@JsonClass(generateAdapter = true)
data class RegisterResponse(
    val userId: Long,
    val accessToken: String,
    val refreshToken: String,
)

fun RegisterResponse.toDomainModel(): RegisterResult = RegisterResult(
    userId = UserId(userId),
    accessToken = accessToken,
    refreshToken = refreshToken,
)
