package com.peekr.presentation.common.util.error

import com.peekr.domain.common.util.ErrorCode
import com.peekr.presentation.R
import com.peekr.presentation.common.util.UiText

fun ErrorCode.asUiText(): UiText = when (this) {
    ErrorCode.Auth.A002 -> UiText.StringResource(R.string.error_code_auth_a002)
    ErrorCode.Unexpected -> UiText.StringResource(R.string.error_unexpected)
}
