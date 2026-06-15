package com.turnin.domain.notification.usecase

import com.turnin.core.domain.announcement.repository.AnnouncementRepository
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.mapError
import com.turnin.core.domain.model.AnnouncementId
import com.turnin.domain.notification.error.NotificationErrorType
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

/**
 * 공지 읽음 처리
 *
 * @see invoke
 */
class MarkAnnouncementAsReadUseCase @Inject constructor(
    private val announcementRepository: AnnouncementRepository,
) {
    /**
     * 공지 읽음 처리를 한다.
     *
     * @param announcementId 공지 ID
     */
    suspend operator fun invoke(announcementId: Long): Result<Unit, NotificationErrorType> = try {
        announcementRepository.markAsRead(AnnouncementId(announcementId))
            .mapError { commonError ->
                NotificationErrorType.CommonError(commonError)
            }
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        Result.Error(NotificationErrorType.Unexpected(e))
    }
}
