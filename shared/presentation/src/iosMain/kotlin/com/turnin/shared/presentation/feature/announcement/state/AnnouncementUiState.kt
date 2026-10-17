package com.turnin.shared.presentation.feature.announcement.state

import com.turnin.shared.presentation.feature.announcement.error.AnnouncementErrorReason
import com.turnin.shared.presentation.feature.announcement.model.UiAnnouncement

/** 공지 목록 UI 상태 */
sealed interface AnnouncementUiState {
    data object Loading : AnnouncementUiState

    data class Success(val announcements: List<UiAnnouncement>) : AnnouncementUiState

    data class Failure(val reason: AnnouncementErrorReason) : AnnouncementUiState
}
