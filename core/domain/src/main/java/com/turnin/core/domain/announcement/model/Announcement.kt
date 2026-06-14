package com.turnin.core.domain.announcement.model

import com.turnin.core.domain.model.AnnouncementId

/**
 * 공지
 *
 * @property id 공지 ID
 * @property title 제목
 * @property content 내용
 * @property targetAudience 수신 대상
 * @property read 읽음 여부
 * @property createdAt 생성 일자
 */
data class Announcement(
    val id: AnnouncementId,
    val title: String,
    val content: String,
    val targetAudience: String,
    val read: Boolean,
    val createdAt: Long,
)
