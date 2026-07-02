package com.turnin.core.data.source.network.social

import com.turnin.core.data.di.GoogleAuth
import com.turnin.core.data.di.KakaoAuth
import com.turnin.core.domain.auth.social.SocialAuthManager
import com.turnin.core.domain.auth.social.SocialAuthManagerFactory
import com.turnin.core.domain.model.SocialLoginProvider
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
