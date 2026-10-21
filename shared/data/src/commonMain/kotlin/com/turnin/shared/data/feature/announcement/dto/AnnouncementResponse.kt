package com.turnin.shared.data.feature.announcement.dto

import com.turnin.shared.domain.feature.announcement.model.Announcement
import com.turnin.shared.domain.feature.announcement.model.AnnouncementAudience
import com.turnin.shared.domain.feature.announcement.model.AnnouncementId
import kotlinx.serialization.Serializable

/**
 * 공지 응답 바디
 *
 * @property id 공지 ID
 * @property title 제목
 * @property content 내용
 * @property targetAudience 수신 대상
 * @property isRead 읽음 여부
 * @property createdAt 생성 일자
 */
@Serializable
internal data class AnnouncementResponse(
    val id: Long,
    val title: String,
    val content: String,
    val targetAudience: String,
    val isRead: Boolean,
    val createdAt: Long,
)

internal fun AnnouncementResponse.toDomainModel(): Announcement =
    Announcement(
        id = AnnouncementId(id),
        title = title,
        content = content,
        targetAudience = AnnouncementAudience.from(targetAudience),
        isRead = isRead,
        createdAt = createdAt,
    )
