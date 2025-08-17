package com.peekr.domain.account.model

/**
 * 회원가입 시 사용 한다.
 *
 * @param provider 소셜로그인 플랫폼 ([SocialLoginProvider])
 * @param providerId 소셜로그인 플랫폼에서 제공하는 고유 ID ([ProviderId])
 * @param displayId 사용자 표시 ID ([DisplayId])
 * @param name 사용자 이름
 * @param profileImageUrl 사용자 프로필 이미지 url
 * @param introduce 사용자 소개 글
 */
data class Register(
    val provider: SocialLoginProvider,
    val providerId: ProviderId,
    val displayId: DisplayId,
    val name: String,
    val profileImageUrl: String,
    val introduce: String,
)
