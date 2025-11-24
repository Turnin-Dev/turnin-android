package com.peekr.core.presentation.error

import com.peekr.core.domain.common.ServerErrorCode
import com.peekr.core.presentation.R
import com.peekr.core.presentation.util.UiText

fun ServerErrorCode.asUiText(): UiText = when (this) {
    ServerErrorCode.Auth.A002 -> UiText.StringResource(R.string.error_code_auth_a002)
    ServerErrorCode.Unexpected -> UiText.StringResource(R.string.error_code_unexpected)
}
