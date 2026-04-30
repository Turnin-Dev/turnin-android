package com.turnin.core.presentation.common.error

import com.turnin.core.domain.common.error.ContractErrorCode
import com.turnin.core.presentation.R
import com.turnin.core.presentation.ui.util.UiText

fun ContractErrorCode.asUiText(): UiText = when (this) {
    ContractErrorCode.Auth.A002 -> UiText.StringResource(R.string.error_code_auth_a002)
    ContractErrorCode.Unexpected -> UiText.StringResource(R.string.error_code_unexpected)
}
