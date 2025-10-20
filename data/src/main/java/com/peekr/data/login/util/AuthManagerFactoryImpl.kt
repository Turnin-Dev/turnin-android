package com.peekr.data.login.util

import com.peekr.core.domain.user.model.SocialLoginProvider
import com.peekr.data.login.di.GoogleAuth
import com.peekr.data.login.di.KakaoAuth
import com.peekr.domain.login.util.AuthManager
import com.peekr.domain.login.util.AuthManagerFactory
import javax.inject.Inject

class AuthManagerFactoryImpl @Inject constructor(
    @GoogleAuth private val googleAuthManager: AuthManager,
    @KakaoAuth private val kakaoAuthManager: AuthManager,
) : AuthManagerFactory {
    override fun create(provider: SocialLoginProvider): AuthManager = when (provider) {
        SocialLoginProvider.GOOGLE -> googleAuthManager
        SocialLoginProvider.KAKAO -> kakaoAuthManager
    }
}
