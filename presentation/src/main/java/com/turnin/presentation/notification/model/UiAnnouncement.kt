package com.turnin.presentation.notification.model

import com.turnin.core.common.util.toRelativeTime
import com.turnin.core.domain.announcement.model.Announcement
import com.turnin.core.domain.announcement.model.AnnouncementAudience

/**
 * UI용 공지 모델
 *
 * @property id 공지 ID
 * @property title 제목
 * @property content 내용
 * @property targetAudience 수신 대상
 * @property isRead 읽음 여부
 * @property createdAt 생성 일자
 */
data class UiAnnouncement(
    val id: Long,
    val title: String,
    val content: String,
    val targetAudience: AnnouncementAudience,
    val isRead: Boolean,
    val createdAt: String,
) {
    companion object {
        val sample = UiAnnouncement(
            id = 1L,
            title = "서버 점검 알림",
            content = "서버 점검 일시: 06/04 (00:00 ~ 00:30)\n" +
                "서버 점검하는 동안 잠시 서버가 불안정 하거나 접속이 불가할 수도 있습니다.",
            targetAudience = AnnouncementAudience.ALL,
            isRead = false,
            createdAt = "2025-04-24",
        )
    }
}

fun Announcement.toUiModel(): UiAnnouncement =
    UiAnnouncement(
        id = id.value,
        title = title,
        content = content,
        targetAudience = targetAudience,
        isRead = isRead,
        createdAt = createdAt.toRelativeTime(false),
    )
