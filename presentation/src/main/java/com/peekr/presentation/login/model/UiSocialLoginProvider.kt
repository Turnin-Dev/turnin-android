package com.peekr.presentation.login.model

import com.peekr.domain.account.model.SocialLoginProvider

/** 소셜로그인 타입 */
enum class UiSocialLoginProvider {
    GOOGLE,
    KAKAO,
    ;

    fun toDomainModel(): SocialLoginProvider = when (this) {
        GOOGLE -> SocialLoginProvider.GOOGLE
        KAKAO -> SocialLoginProvider.KAKAO
    }
}
