package com.peekr.presentation.setting.model

import com.peekr.core.domain.model.SocialLoginProvider
import com.peekr.domain.setting.model.AccountInfo

/**
 * UI용 계정 정보
 *
 * @property userId 사용자 ID
 * @property displayId 사용자 표시 ID
 * @property name 사용자 명
 * @property profileImageUrl 프로필 사진 URL
 * @property introduce 소개글
 * @property loginProvider 로그인 타입
 */
data class UiAccountInfo(
    val userId: Long,
    val displayId: String,
    val name: String,
    val profileImageUrl: String?,
    val introduce: String,
    val loginProvider: SocialLoginProvider,
) {
    companion object {
        val sample = UiAccountInfo(
            userId = 1,
            displayId = "DisplayID",
            name = "Name",
            profileImageUrl = null,
            introduce = "Introduce, Introduce, Introduce",
            loginProvider = SocialLoginProvider.GOOGLE,
        )
    }
}

fun AccountInfo.toUiModel(): UiAccountInfo =
    UiAccountInfo(
        userId = userId.value,
        displayId = displayId.value,
        name = name.value,
        profileImageUrl = profileImageUrl,
        introduce = introduce.value,
        loginProvider = loginProvider,
    )
