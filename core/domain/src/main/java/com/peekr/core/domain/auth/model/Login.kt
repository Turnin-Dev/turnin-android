package com.peekr.core.domain.auth.model

import com.peekr.core.domain.user.model.ProviderId
import com.peekr.core.domain.user.model.SocialLoginProvider

/**
 * 로그인 시 사용 한다.
 *
 * @param provider 소셜로그인 플랫폼 ([com.peekr.domain.account.model.SocialLoginProvider])
 * @param providerId 소셜로그인 플랫폼에서 제공하는 고유 ID ([com.peekr.domain.account.model.ProviderId])
 */
data class Login(
    val provider: SocialLoginProvider,
    val providerId: ProviderId,
)
