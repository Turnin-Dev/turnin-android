package com.peekr.presentation.shared.util.error

import com.peekr.domain.shared.util.ErrorCode
import com.peekr.presentation.R
import com.peekr.presentation.shared.util.UiText

fun ErrorCode.asUiText(): UiText = when (this) {
    ErrorCode.Auth.A002 -> UiText.StringResource(R.string.error_code_auth_a002)
    ErrorCode.Unexpected -> UiText.StringResource(R.string.error_code_unexpected)
}
