package com.turnin.presentation.notification.error

import com.turnin.core.presentation.common.error.asUiText
import com.turnin.core.presentation.ui.util.UiText
import com.turnin.core.presentation.ui.util.UiText.StringResource
import com.turnin.domain.notification.error.NotificationErrorType
import com.turnin.presentation.R

fun NotificationErrorType.asUiText(): UiText = when (this) {
    is NotificationErrorType.Unexpected -> StringResource(R.string.notification_error_unexpected)
    is NotificationErrorType.CommonError -> this.error.asUiText()
}
