package com.peekr.core.domain.notification.model

import com.peekr.core.domain.model.NotificationId
import com.peekr.core.domain.model.NotificationType

/**
 * 알림 모델
 *
 * @property id 알림 ID
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
data class Notification(
    val id: NotificationId,
    val notiType: NotificationType,
    val title: String?,
    val message: String,
    val imageUrl: String?,
    val isRead: Boolean,
    val isBroadcast: Boolean,
    val refId: Long?,
    val refType: String?,
    val createdAt: Long,
)
