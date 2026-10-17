package com.turnin.shared.feature.announcement.usecase

import com.turnin.shared.core.result.Result
import com.turnin.shared.core.result.mapError
import com.turnin.shared.feature.announcement.data.AnnouncementRepository
import com.turnin.shared.feature.announcement.error.AnnouncementErrorType
import com.turnin.shared.feature.announcement.model.Announcement
import kotlinx.coroutines.flow.Flow

/**
 * 공지 목록 조회
 *
 * @see invoke
 */
class GetAnnouncementsUseCase internal constructor(
    private val announcementRepository: AnnouncementRepository,
) {
    /**
     * 공지 목록을 조회한다.
     */
    operator fun invoke(): Flow<Result<List<Announcement>, AnnouncementErrorType>> =
        announcementRepository.getAnnouncements()
            .mapError { networkError ->
                AnnouncementErrorType.Network(networkError)
            }
}
