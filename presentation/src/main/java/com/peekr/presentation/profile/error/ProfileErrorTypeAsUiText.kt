package com.peekr.presentation.profile.error

import com.peekr.core.presentation.common.error.asUiText
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.core.presentation.ui.util.UiText.StringResource
import com.peekr.domain.profile.error.ProfileErrorType
import com.peekr.presentation.R

internal fun ProfileErrorType.asUiText(): UiText = when (this) {
    is ProfileErrorType.CommonError -> this.error.asUiText()
    is ProfileErrorType.ValidationError -> this.error.asUiText()
    ProfileErrorType.MyUserIdNotFound -> StringResource(R.string.profile_error_my_user_id_not_found)
    ProfileErrorType.ProfileLoadFailed -> StringResource(R.string.profile_error_profile_load_failed)
    is ProfileErrorType.Unexpected -> StringResource(R.string.profile_error_unexpected)
    ProfileErrorType.UpdateUserKeywordOffsetFailed ->
        StringResource(R.string.profile_error_update_user_keyword_offset_failed)
}
