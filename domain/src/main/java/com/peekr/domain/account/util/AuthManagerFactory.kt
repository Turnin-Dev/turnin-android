package com.peekr.domain.account.util

import com.peekr.domain.account.model.SocialLoginProvider

interface AuthManagerFactory {
    fun create(provider: SocialLoginProvider): AuthManager
}
