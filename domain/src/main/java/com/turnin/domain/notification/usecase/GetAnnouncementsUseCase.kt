package com.turnin.domain.notification.usecase

import com.turnin.core.domain.announcement.model.Announcement
import com.turnin.core.domain.announcement.repository.AnnouncementRepository
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.mapError
import com.turnin.domain.notification.error.NotificationErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * 공지 목록 조회
 *
 * @see invoke
 */
class GetAnnouncementsUseCase @Inject constructor(
    private val announcementRepository: AnnouncementRepository,
) {
    /**
     * 공지 목록을 조회한다.
     */
    operator fun invoke(): Flow<Result<List<Announcement>, NotificationErrorType>> =
        announcementRepository.getAnnouncements()
            .mapError { commonError ->
                NotificationErrorType.CommonError(commonError)
            }
}
