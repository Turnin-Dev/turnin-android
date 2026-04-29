package com.turnin.core.presentation.ui.model

import kotlinx.serialization.Serializable

/** UI용 소셜로그인 타입 */
@Serializable
enum class UiSocialLoginProvider {
    GOOGLE,
    KAKAO,
}
