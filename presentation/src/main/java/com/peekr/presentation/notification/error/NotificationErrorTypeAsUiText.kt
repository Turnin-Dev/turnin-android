package com.peekr.presentation.notification.error

import com.peekr.core.presentation.common.error.asUiText
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.core.presentation.ui.util.UiText.StringResource
import com.peekr.domain.notification.error.NotificationErrorType
import com.peekr.presentation.R

fun NotificationErrorType.asUiText(): UiText = when (this) {
    is NotificationErrorType.Unexpected -> StringResource(R.string.notification_error_unexpected)
    is NotificationErrorType.CommonError -> this.error.asUiText()
}
