package com.peekr.presentation.login.mapper

import com.peekr.core.domain.user.model.SocialLoginProvider
import com.peekr.core.presentation.model.UiSocialLoginProvider
import com.peekr.core.presentation.model.UiSocialLoginProvider.GOOGLE
import com.peekr.core.presentation.model.UiSocialLoginProvider.KAKAO

fun UiSocialLoginProvider.toDomainModel(): SocialLoginProvider = when (this) {
    GOOGLE -> SocialLoginProvider.GOOGLE
    KAKAO -> SocialLoginProvider.KAKAO
}

fun SocialLoginProvider.toUiModel(): UiSocialLoginProvider = when (this) {
    SocialLoginProvider.GOOGLE -> GOOGLE
    SocialLoginProvider.KAKAO -> KAKAO
}
