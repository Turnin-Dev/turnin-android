package com.peekr.presentation.login.error

import com.peekr.core.presentation.common.error.asUiText
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.core.presentation.ui.util.UiText.StringResource
import com.peekr.domain.login.error.LoginErrorType
import com.peekr.presentation.R

fun LoginErrorType.asUiText(): UiText = when (this) {
    LoginErrorType.LoginFailed -> StringResource(R.string.login_error_login_failed)
    is LoginErrorType.Unexpected -> StringResource(R.string.login_error_unexpected)
    is LoginErrorType.CommonError -> this.error.asUiText()
}
