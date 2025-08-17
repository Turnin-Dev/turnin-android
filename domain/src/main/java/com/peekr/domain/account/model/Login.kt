package com.peekr.domain.account.model

/**
 * 로그인 시 사용 한다.
 *
 * @param provider 소셜로그인 플랫폼 ([SocialLoginProvider])
 * @param providerId 소셜로그인 플랫폼에서 제공하는 고유 ID ([ProviderId])
 */
data class Login(
    val provider: SocialLoginProvider,
    val providerId: ProviderId,
)
