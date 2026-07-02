package com.turnin.presentation.login.error

import com.turnin.core.presentation.common.error.asUiText
import com.turnin.core.presentation.ui.util.UiText
import com.turnin.core.presentation.ui.util.UiText.StringResource
import com.turnin.domain.login.error.LoginErrorType
import com.turnin.presentation.R

fun LoginErrorType.asUiText(): UiText = when (this) {
    LoginErrorType.LoginFailed -> StringResource(R.string.login_error_login_failed)
    is LoginErrorType.Unexpected -> StringResource(R.string.login_error_unexpected)
    is LoginErrorType.CommonError -> this.error.asUiText()
}
