package com.peekr.core.domain.auth.social

import com.peekr.core.domain.model.SocialLoginProvider

/**
 * 소셜로그인 플랫폼에 맞는 [SocialAuthManager]를 생성한다.
 *
 * @see SocialLoginProvider
 * @see SocialAuthManager
 */
interface SocialAuthManagerFactory {
    fun create(provider: SocialLoginProvider): SocialAuthManager
}
