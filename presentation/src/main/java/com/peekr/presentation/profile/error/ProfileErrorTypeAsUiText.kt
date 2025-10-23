package com.peekr.presentation.profile.error

import com.peekr.core.presentation.error.asUiText
import com.peekr.core.presentation.util.UiText
import com.peekr.domain.profile.error.ProfileErrorType
import com.peekr.presentation.R

internal fun ProfileErrorType.asUiText(): UiText = when (this) {
    is ProfileErrorType.CommonError -> this.error.asUiText()
    is ProfileErrorType.UserError -> this.error.asUiText()
    is ProfileErrorType.UserKeywordError -> this.error.asUiText()
    ProfileErrorType.UserNotFound -> UiText.StringResource(R.string.profile_error_user_not_found)
    is ProfileErrorType.Unexpected -> UiText.StringResource(R.string.profile_error_unexpected)
}
