package com.turnin.domain.notification.usecase

import javax.inject.Inject

class NotificationUseCases @Inject constructor(
    /**
     * 알림 목록 조회
     * @see GetNotificationsUseCase
     */
    val getNotifications: GetNotificationsUseCase,
    /**
     * 알림 읽음 처리
     * @see MarkAsReadUseCase
     */
    val markAsRead: MarkAsReadUseCase,
)
