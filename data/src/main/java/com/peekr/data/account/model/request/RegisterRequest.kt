package com.peekr.data.account.model.request

import com.peekr.domain.account.model.Register
import com.peekr.domain.account.model.SocialLoginProvider
import com.squareup.moshi.JsonClass

/**
 * 회원가입 요청 바디
 *
 * @param provider 소셜로그인 플랫폼 ([SocialLoginProvider])
 * @param providerId 소셜로그인 플랫폼에서 제공하는 고유 ID
 * @param displayId 사용자 표시 ID
 * @param name 사용자 이름
 * @param profileImageUrl 사용자 프로필 이미지 url
 * @param introduce 사용자 소개 글
 */
@JsonClass(generateAdapter = true)
data class RegisterRequest(
    val provider: SocialLoginProvider,
    val providerId: String,
    val displayId: String,
    val name: String,
    val profileImageUrl: String?,
    val introduce: String?,
)

fun Register.toDataModel(): RegisterRequest =
    RegisterRequest(
        provider = provider,
        providerId = providerId.uid,
        displayId = displayId.id,
        name = name.name,
        profileImageUrl = profileImageUrl,
        introduce = introduce,
    )
