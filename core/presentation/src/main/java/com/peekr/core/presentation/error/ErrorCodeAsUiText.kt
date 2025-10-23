package com.peekr.core.presentation.error

import com.peekr.core.domain.util.ErrorCode
import com.peekr.core.presentation.R
import com.peekr.core.presentation.util.UiText

fun ErrorCode.asUiText(): UiText = when (this) {
    ErrorCode.Auth.A002 -> UiText.StringResource(R.string.error_code_auth_a002)
    ErrorCode.Unexpected -> UiText.StringResource(R.string.error_code_unexpected)
}
