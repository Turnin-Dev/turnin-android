package com.turnin.presentation.discover.error

import com.turnin.core.presentation.common.error.asUiText
import com.turnin.core.presentation.ui.util.UiText
import com.turnin.core.presentation.ui.util.UiText.StringResource
import com.turnin.domain.discover.error.DiscoverErrorType
import com.turnin.presentation.R

fun DiscoverErrorType.asUiText(): UiText = when (this) {
    DiscoverErrorType.MyProfileNotFound -> StringResource(R.string.discover_error_my_profile_not_found)
    DiscoverErrorType.NotSelectedTarget -> StringResource(R.string.discover_error_not_selected_target)
    DiscoverErrorType.MyKeywordsRefreshFailed -> StringResource(R.string.discover_error_my_keywords_refresh_failed)
    is DiscoverErrorType.Unexpected -> StringResource(R.string.discover_error_unexpected)
    is DiscoverErrorType.CommonError -> this.error.asUiText()
}
