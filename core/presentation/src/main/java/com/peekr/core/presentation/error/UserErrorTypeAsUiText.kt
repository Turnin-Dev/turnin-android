package com.peekr.core.presentation.error

import com.peekr.core.domain.user.error.UserErrorType
import com.peekr.core.presentation.R
import com.peekr.core.presentation.util.UiText

fun UserErrorType.asUiText(): UiText = when (this) {
    is UserErrorType.CommonError -> this.error.asUiText()
    is UserErrorType.Unexpected -> UiText.StringResource(R.string.user_error_unexpected)
}
