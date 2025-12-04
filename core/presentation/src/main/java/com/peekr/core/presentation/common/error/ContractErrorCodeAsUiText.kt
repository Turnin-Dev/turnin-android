package com.peekr.core.presentation.common.error

import com.peekr.core.domain.common.error.ContractErrorCode
import com.peekr.core.presentation.R
import com.peekr.core.presentation.ui.util.UiText

fun ContractErrorCode.asUiText(): UiText = when (this) {
    ContractErrorCode.Auth.A002 -> UiText.StringResource(R.string.error_code_auth_a002)
    ContractErrorCode.Unexpected -> UiText.StringResource(R.string.error_code_unexpected)
}
