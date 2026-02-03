package com.peekr.presentation.discover.error

import com.peekr.core.presentation.ui.util.UiText
import com.peekr.core.presentation.ui.util.UiText.StringResource
import com.peekr.domain.discover.error.DiscoverErrorType
import com.peekr.presentation.R

fun DiscoverErrorType.asUiText(): UiText = when (this) {
    DiscoverErrorType.MyProfileNotFound -> StringResource(R.string.discover_error_my_profile_not_found)
    DiscoverErrorType.NotSelectedTarget -> StringResource(R.string.discover_error_not_selected_target)
    is DiscoverErrorType.Unexpected -> StringResource(R.string.discover_error_unexpected)
}
