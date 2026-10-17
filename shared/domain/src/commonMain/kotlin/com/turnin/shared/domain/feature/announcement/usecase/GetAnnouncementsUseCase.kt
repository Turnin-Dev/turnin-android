package com.turnin.shared.domain.feature.announcement.usecase

import com.turnin.shared.domain.core.result.Result
import com.turnin.shared.domain.core.result.mapError
import com.turnin.shared.domain.feature.announcement.error.AnnouncementErrorType
import com.turnin.shared.domain.feature.announcement.model.Announcement
import com.turnin.shared.domain.feature.announcement.repository.AnnouncementRepository
import kotlinx.coroutines.flow.Flow

/**
 * 공지 목록 조회
 *
 * @see invoke
 */
class GetAnnouncementsUseCase(
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
