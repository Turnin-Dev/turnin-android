package com.peekr.core.presentation.model

import kotlinx.serialization.Serializable

/** UI용 소셜로그인 타입 */
@Serializable
enum class UiSocialLoginProvider {
    GOOGLE,
    KAKAO,
}
