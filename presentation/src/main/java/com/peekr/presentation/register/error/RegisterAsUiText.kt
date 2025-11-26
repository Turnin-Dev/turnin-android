package com.peekr.presentation.register.error

import com.peekr.core.presentation.common.error.asUiText
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.core.presentation.ui.util.UiText.StringResource
import com.peekr.domain.register.error.RegisterErrorType
import com.peekr.presentation.R
import com.peekr.presentation.login.error.asUiText

internal fun RegisterErrorType.asUiText(): UiText = when (this) {
    RegisterErrorType.CantUseEmptyOrBlank -> StringResource(R.string.register_error_cant_use_display_id)
    RegisterErrorType.DisplayIdNotAvailable -> StringResource(R.string.register_error_cant_use_empty_or_blank)
    RegisterErrorType.ImageFileIsNull -> StringResource(R.string.register_error_image_file_is_null)
    is RegisterErrorType.CommonError -> this.error.asUiText()
    is RegisterErrorType.Unexpected -> {
        StringResource(R.string.register_error_unexpected)
    }
}
