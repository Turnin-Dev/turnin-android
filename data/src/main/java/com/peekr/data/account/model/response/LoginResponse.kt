package com.peekr.data.account.model.response

import com.peekr.domain.account.model.JWTToken
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
) {
    /**
     * 현재 LoginResponse 객체를 JWTToken 도메인 모델로 변환합니다.
     *
     * @return accessToken과 refreshToken 값을 포함하는 JWTToken 객체
     */
    fun toDomainModel(): JWTToken = JWTToken(
        accessToken = accessToken,
        refreshToken = refreshToken,
    )
}
