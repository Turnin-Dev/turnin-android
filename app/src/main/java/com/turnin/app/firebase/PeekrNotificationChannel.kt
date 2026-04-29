package com.turnin.app.firebase

import com.turnin.core.domain.model.NotificationType

object PeekrNotificationChannel {
    const val HIGH_ID = "peekr_notification_channel_high"
    const val NORMAL_ID = "peekr_notification_channel_normal"

    fun getChannelId(notiType: NotificationType?): String = when (notiType) {
        NotificationType.FRIEND_REQUEST,
        NotificationType.FRIEND_ACCEPT,
        -> HIGH_ID

        else -> NORMAL_ID
    }
}
