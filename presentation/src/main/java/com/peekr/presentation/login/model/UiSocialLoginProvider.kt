package com.peekr.presentation.login.model

import com.peekr.domain.account.model.SocialLoginProvider
import com.peekr.presentation.login.model.UiSocialLoginProvider.GOOGLE
import com.peekr.presentation.login.model.UiSocialLoginProvider.KAKAO
import kotlinx.serialization.Serializable

/** UI용 소셜로그인 타입 */
@Serializable
enum class UiSocialLoginProvider {
    GOOGLE,
    KAKAO,
}

fun UiSocialLoginProvider.toDomainModel(): SocialLoginProvider = when (this) {
    GOOGLE -> SocialLoginProvider.GOOGLE
    KAKAO -> SocialLoginProvider.KAKAO
}

fun SocialLoginProvider.toUiModel(): UiSocialLoginProvider = when (this) {
    SocialLoginProvider.GOOGLE -> GOOGLE
    SocialLoginProvider.KAKAO -> KAKAO
}
