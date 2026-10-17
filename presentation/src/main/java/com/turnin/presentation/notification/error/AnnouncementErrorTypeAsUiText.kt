package com.turnin.presentation.notification.error

import com.turnin.core.presentation.common.error.asUiText
import com.turnin.core.presentation.ui.util.UiText
import com.turnin.core.presentation.ui.util.UiText.StringResource
import com.turnin.presentation.R
import com.turnin.shared.domain.feature.announcement.error.AnnouncementErrorType

fun AnnouncementErrorType.asUiText(): UiText = when (this) {
    is AnnouncementErrorType.Unexpected -> StringResource(R.string.notification_error_unexpected)
    is AnnouncementErrorType.Network -> this.error.asUiText()
}
