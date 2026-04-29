package com.turnin.domain.notification.usecase

import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.mapError
import com.turnin.core.domain.model.NotificationId
import com.turnin.core.domain.notification.repository.NotificationRepository
import com.turnin.domain.notification.error.NotificationErrorType
import javax.inject.Inject
import kotlinx.coroutines.CancellationException

/**
 * 알림 읽음 처리
 *
 * @see invoke
 */
class MarkAsReadUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
) {
    /**
     * 알림 읽음 처리한다.
     *
     * @param notificationId 알림 ID
     */
    suspend operator fun invoke(notificationId: Long): Result<Unit, NotificationErrorType> = try {
        val notificationIdVO = NotificationId(notificationId)
        notificationRepository.markAsRead(notificationIdVO)
            .mapError { commonError ->
                NotificationErrorType.CommonError(commonError)
            }
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        Result.Error(NotificationErrorType.Unexpected(e))
    }
}
