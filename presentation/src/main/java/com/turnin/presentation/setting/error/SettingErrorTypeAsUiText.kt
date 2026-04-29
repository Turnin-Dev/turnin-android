package com.turnin.presentation.setting.error

import com.turnin.core.presentation.common.error.asUiText
import com.turnin.core.presentation.ui.util.UiText
import com.turnin.core.presentation.ui.util.UiText.StringResource
import com.turnin.domain.setting.error.SettingErrorType
import com.turnin.presentation.R

fun SettingErrorType.asUiText(): UiText = when (this) {
    SettingErrorType.MyProfileNotFound -> StringResource(R.string.setting_error_my_profile_not_found)
    SettingErrorType.DisplayIdNotAvailable -> StringResource(R.string.setting_error_display_id_not_available)
    SettingErrorType.UploadImageFailed -> StringResource(R.string.setting_error_upload_image_failed)
    is SettingErrorType.Unexpected -> StringResource(R.string.setting_error_unexpected)
    is SettingErrorType.CommonError -> this.error.asUiText()
    is SettingErrorType.ValidationError -> this.error.asUiText()
}
