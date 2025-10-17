package com.peekr.presentation.profile.util

import com.peekr.core.presentation.util.UiText
import com.peekr.core.presentation.util.UiText.StringResource
import com.peekr.domain.profile.error.ProfileErrorType
import com.peekr.presentation.R

fun ProfileErrorType.asUiText(): UiText = when (this) {
    ProfileErrorType.UserIdNotFound -> {
        StringResource(R.string.profile_screen_error_type_user_id_not_found)
    }
}
