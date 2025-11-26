package com.peekr.presentation.profile.error

import com.peekr.core.presentation.ui.util.UiText
import com.peekr.core.presentation.ui.util.UiText.StringResource
import com.peekr.domain.profile.error.ProfileErrorType
import com.peekr.presentation.R
import com.peekr.presentation.login.error.asUiText

internal fun ProfileErrorType.asUiText(): UiText = when (this) {
    is ProfileErrorType.CommonError -> this.error.asUiText()
    is ProfileErrorType.UserError -> this.error.asUiText()
    is ProfileErrorType.UserKeywordError -> this.error.asUiText()
    is ProfileErrorType.ValidationError -> this.error.asUiText()
    ProfileErrorType.UserNotFound -> StringResource(R.string.profile_error_user_not_found)
    is ProfileErrorType.Unexpected -> StringResource(R.string.profile_error_unexpected)
    ProfileErrorType.UpdateUserKeywordOffsetFailed ->
        StringResource(R.string.profile_error_update_user_keyword_offset_failed)
}
