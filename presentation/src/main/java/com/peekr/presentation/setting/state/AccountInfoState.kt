package com.peekr.presentation.setting.state

import com.peekr.domain.setting.model.SettingProfileImagePatch
import com.peekr.presentation.setting.model.UiAccountInfo

/**
 * 계정 정보 UI 상태
 *
 * @property accountInfo 계정 정보
 * @property isAccountInfoEdited 계정 정보 수정 여부
 */
data class AccountInfoState(
    val accountInfo: UiAccountInfo? = null,
    val profileImagePatch: SettingProfileImagePatch = SettingProfileImagePatch.Unchanged,
    val isAccountInfoEdited: Boolean = false,
)
