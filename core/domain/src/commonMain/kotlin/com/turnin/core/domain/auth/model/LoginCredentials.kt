package com.turnin.core.domain.auth.model

import com.turnin.core.domain.model.ProviderId
import com.turnin.core.domain.model.SocialLoginProvider

/**
 * 로그인 시 사용 한다.
 *
 * @param provider 소셜로그인 플랫폼 ([SocialLoginProvider])
 * @param providerId 소셜로그인 플랫폼에서 제공하는 고유 ID ([ProviderId])
 */
data class LoginCredentials(
    val provider: SocialLoginProvider,
    val providerId: ProviderId,
)
