package com.turnin.shared.presentation.feature.announcement.model

import com.turnin.shared.domain.feature.announcement.model.Announcement

/**
 * iOS UI용 공지 모델 (Android의 `UiAnnouncement`와 같은 역할)
 *
 * 날짜 표시 형식은 Swift에서 정하므로 epoch millis를 그대로 넘긴다.
 *
 * @property id 공지 ID
 * @property title 제목
 * @property content 내용
 * @property isRead 읽음 여부
 * @property createdAtEpochMillis 생성 시각 (epoch millis)
 */
data class UiAnnouncement(
    val id: Long,
    val title: String,
    val content: String,
    val isRead: Boolean,
    val createdAtEpochMillis: Long,
)

internal fun Announcement.toUiModel(): UiAnnouncement = UiAnnouncement(
    id = id.value,
    title = title,
    content = content,
    isRead = isRead,
    createdAtEpochMillis = createdAt,
)
