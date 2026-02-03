package com.peekr.presentation.discover.error

import com.peekr.core.presentation.ui.util.UiText
import com.peekr.domain.discover.error.DiscoverErrorType
import com.peekr.presentation.R

fun DiscoverErrorType.asUiText(): UiText = when (this) {
    DiscoverErrorType.MyProfileNotFound -> UiText.StringResource(R.string.discover_error_my_profile_not_found)
    is DiscoverErrorType.Unexpected -> UiText.StringResource(R.string.discover_error_unexpected)
}
