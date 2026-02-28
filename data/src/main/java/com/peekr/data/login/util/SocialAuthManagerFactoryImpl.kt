package com.peekr.data.login.util

import com.peekr.core.domain.model.SocialLoginProvider
import com.peekr.data.login.di.GoogleAuth
import com.peekr.data.login.di.KakaoAuth
import com.peekr.domain.login.util.SocialAuthManager
import com.peekr.domain.login.util.SocialAuthManagerFactory
import javax.inject.Inject

class SocialAuthManagerFactoryImpl @Inject constructor(
    @GoogleAuth private val googleSocialAuthManager: SocialAuthManager,
    @KakaoAuth private val kakaoSocialAuthManager: SocialAuthManager,
) : SocialAuthManagerFactory {
    override fun create(provider: SocialLoginProvider): SocialAuthManager = when (provider) {
        SocialLoginProvider.GOOGLE -> googleSocialAuthManager
        SocialLoginProvider.KAKAO -> kakaoSocialAuthManager
    }
}
