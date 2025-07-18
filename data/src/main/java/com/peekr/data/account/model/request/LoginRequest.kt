package com.peekr.data.account.model.request

import com.peekr.domain.account.model.Login
import com.peekr.domain.account.model.SocialLoginProvider
import com.squareup.moshi.JsonClass

/**
 * 로그인 요청 바디
 *
 * @property provider 소셜로그인 플랫폼
 * @property providerId 소셜로그인 플랫폼에서 제공된 id
 */
@JsonClass(generateAdapter = true)
data class LoginRequest(
    val provider: SocialLoginProvider,
    val providerId: String,
)

/**
 * Login 도메인 모델을 LoginRequest 데이터 모델로 변환합니다.
 *
 * @return 변환된 LoginRequest 인스턴스
 */
fun Login.toDataModel(): LoginRequest = LoginRequest(provider, providerId.uid)
