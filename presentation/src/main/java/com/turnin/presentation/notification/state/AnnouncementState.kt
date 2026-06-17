package com.turnin.presentation.notification.state

import com.turnin.core.presentation.ui.util.UiText
import com.turnin.presentation.notification.model.UiAnnouncement

/**
 * 공지 상태 클래스
 *
 * @property announcements 공지 목록
 * @property loading 로딩 여부
 * @property error 에러 상태
 */
data class AnnouncementState(
    val announcements: List<UiAnnouncement> = emptyList(),
    val loading: Boolean = false,
    val error: UiText? = null,
)
