package com.peekr.presentation.login.model

import com.peekr.domain.account.model.SocialLoginProvider

/** 소셜로그인 타입 */
enum class UiSocialLoginProvider {
    Google,
    Kakao,
    ;

    fun toDomainModel(): SocialLoginProvider = when (this) {
        Google -> SocialLoginProvider.GOOGLE
        Kakao -> SocialLoginProvider.KAKAO
    }
}
