package com.peekr.core.presentation.common.error

import com.peekr.core.domain.user.error.UserErrorType
import com.peekr.core.presentation.R
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.core.presentation.ui.util.UiText.StringResource

fun UserErrorType.asUiText(): UiText = when (this) {
    is UserErrorType.CommonError -> this.error.asUiText()
    is UserErrorType.Unexpected -> StringResource(R.string.user_error_unexpected)
    UserErrorType.UserIdNotFound -> StringResource(R.string.user_error_user_id_not_found)
}
