package com.turnin.core.data.source.network.dto.auth.response

import com.squareup.moshi.JsonClass
import com.turnin.core.domain.auth.model.LoginResult
import com.turnin.core.domain.model.UserId

/**
 * 로그인 응답 바디
 *
 * @property accessToken JWT 형식의 액세스 토큰
 * @property refreshToken JWT 형식의 리프레쉬 토큰
 */
@JsonClass(generateAdapter = true)
data class LoginResponse(
    val userId: Long,
    val accessToken: String,
    val refreshToken: String,
)

fun LoginResponse.toDomainModel(): LoginResult = LoginResult(
    userId = UserId(userId),
    accessToken = accessToken,
    refreshToken = refreshToken,
)
