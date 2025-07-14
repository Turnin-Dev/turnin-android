package com.peekr.data.account.util

import com.peekr.data.account.di.GoogleAuth
import com.peekr.data.account.di.KakaoAuth
import com.peekr.domain.account.model.SocialLoginProvider
import com.peekr.domain.account.util.AuthManager
import com.peekr.domain.account.util.AuthManagerFactory
import javax.inject.Inject

class AuthManagerFactoryImpl @Inject constructor(
    @GoogleAuth private val googleAuthManager: AuthManager,
    @KakaoAuth private val kakaoAuthManager: AuthManager,
) : AuthManagerFactory {
    override fun create(provider: SocialLoginProvider): AuthManager = when (provider) {
        SocialLoginProvider.Google -> googleAuthManager
        SocialLoginProvider.Kakao -> kakaoAuthManager
    }
}
