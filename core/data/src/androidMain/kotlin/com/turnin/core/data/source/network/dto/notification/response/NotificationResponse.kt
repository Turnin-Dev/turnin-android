package com.turnin.core.data.source.network.dto.notification.response

import com.squareup.moshi.JsonClass
import com.turnin.core.domain.model.NotificationId
import com.turnin.core.domain.model.NotificationType
import com.turnin.core.domain.model.UserId
import com.turnin.core.domain.notification.model.Notification

/**
 * 알림 응답 바디
 *
 * @property id 알림 ID
 * @property userId 사용자 ID
 * @property notiType 알림 유형 [NotificationType]
 * @property title 알림 제목
 * @property message 알림 메시지
 * @property imageUrl 알림 이미지 URL
 * @property isRead 알림 읽음 여부
 * @property isBroadcast 브로드캐스트 여부
 * @property refId 참조 ID
 * @property refType 참조 타입
 * @property createdAt 생성 시간
 */
@JsonClass(generateAdapter = true)
data class NotificationResponse(
    val id: Long,
    val userId: Long?,
    val notiType: String,
    val title: String?,
    val message: String,
    val imageUrl: String?,
    val isRead: Boolean,
    val isBroadcast: Boolean,
    val refId: Long?,
    val refType: String?,
    val createdAt: Long,
) {
    fun toDomainModel() = Notification(
        id = NotificationId(this.id),
        userId = this.userId?.let { UserId(it) },
        notiType = runCatching { NotificationType.valueOf(this.notiType) }
            .getOrDefault(NotificationType.NOTICE),
        title = this.title,
        message = this.message,
        imageUrl = this.imageUrl,
        isRead = this.isRead,
        isBroadcast = this.isBroadcast,
        refId = this.refId,
        refType = this.refType,
        createdAt = this.createdAt,
    )
}
