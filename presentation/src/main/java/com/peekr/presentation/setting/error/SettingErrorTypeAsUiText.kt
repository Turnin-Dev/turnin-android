package com.peekr.presentation.setting.error

import com.peekr.core.presentation.common.error.asUiText
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.core.presentation.ui.util.UiText.StringResource
import com.peekr.domain.setting.error.SettingErrorType
import com.peekr.presentation.R

fun SettingErrorType.asUiText(): UiText = when (this) {
    SettingErrorType.LoginProviderNotFound -> StringResource(R.string.setting_error_login_provider_not_found)
    SettingErrorType.MyProfileNotFound -> StringResource(R.string.setting_error_login_my_profile_not_found)
    is SettingErrorType.Unexpected -> StringResource(R.string.setting_error_unexpected)
    is SettingErrorType.CommonError -> this.error.asUiText()
}
