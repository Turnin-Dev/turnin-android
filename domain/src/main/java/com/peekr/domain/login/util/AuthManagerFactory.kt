package com.peekr.domain.login.util

import com.peekr.core.domain.model.SocialLoginProvider

/**
 * 소셜로그인 플랫폼에 맞는 [AuthManager]를 생성한다.
 *
 * @see SocialLoginProvider
 * @see AuthManager
 */
interface AuthManagerFactory {
    fun create(provider: SocialLoginProvider): AuthManager
}
