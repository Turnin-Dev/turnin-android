package com.peekr.core.data.source.network.social

import com.peekr.core.data.di.GoogleAuth
import com.peekr.core.data.di.KakaoAuth
import com.peekr.core.domain.auth.social.SocialAuthManager
import com.peekr.core.domain.auth.social.SocialAuthManagerFactory
import com.peekr.core.domain.model.SocialLoginProvider
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
