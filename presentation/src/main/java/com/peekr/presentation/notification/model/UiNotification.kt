package com.peekr.presentation.notification.model

import com.peekr.core.common.logger.AppLogger
import com.peekr.core.common.util.toRelativeTime
import com.peekr.core.domain.model.NotificationType
import com.peekr.core.domain.notification.model.Notification
import com.peekr.core.presentation.common.navigation.deepLink.DeepLink

/**
 * UI용 알림 모델
 *
 * @property id 알림 ID
 * @property title 알림 제목
 * @property message 알림 메시지
 * @property imageUrl 알림 이미지 URL
 * @property isRead 알림 읽음 여부
 * @property isBroadcast 브로드캐스트 여부
 * @property deepLink 딥링크 URI
 * @property createdAt 생성 시간
 */
data class UiNotification(
    val id: Long,
    val notiType: NotificationType,
    val title: String?,
    val message: String,
    val imageUrl: String?,
    val isRead: Boolean,
    val isBroadcast: Boolean,
    val deepLink: String,
    val createdAt: String,
) {
    companion object {
        val sample = UiNotification(
            id = 1L,
            notiType = NotificationType.NOTICE,
            title = "새 알림",
            message = "새로운 알림 메시지입니다.",
            imageUrl = null,
            isRead = false,
            isBroadcast = false,
            deepLink = "",
            createdAt = "2026-03-29",
        )
    }
}

fun Notification.toUiModel(): UiNotification =
    UiNotification(
        id = id.value,
        notiType = notiType,
        title = title,
        message = message,
        imageUrl = imageUrl,
        isRead = isRead,
        isBroadcast = isBroadcast,
        deepLink = when {
            notiType.isFriendRelated -> {
                DeepLink.Builder.profile(userId = refId)
            }

            notiType == NotificationType.NEW_KEYWORD -> {
                DeepLink.Builder.keywordDetail(userKeywordId = refId, userId = userId?.value)
            }

            else -> {
                AppLogger.w(
                    "NotificationDeepLinkParse",
                    "딥링크 생성 실패: notiType=$notiType, refId=$refId, userId=$userId",
                )
                DeepLink.Builder.notifications()
            }
        } ?: DeepLink.Builder.notifications(),
        createdAt = createdAt.toRelativeTime(false),
    )
