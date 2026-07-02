package com.turnin.domain.notification.usecase

import androidx.paging.PagingData
import com.turnin.core.domain.notification.model.Notification
import com.turnin.core.domain.notification.repository.NotificationRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * 알림 목록 조회
 *
 * @see invoke
 */
class GetNotificationsUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
) {
    /**
     * 알림 목록을 조회한다.
     */
    operator fun invoke(): Flow<PagingData<Notification>> =
        notificationRepository.getNotifications()
}
