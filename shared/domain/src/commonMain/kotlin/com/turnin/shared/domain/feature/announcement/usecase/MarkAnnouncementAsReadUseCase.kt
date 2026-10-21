package com.turnin.shared.domain.feature.announcement.usecase

import com.turnin.shared.domain.core.result.Result
import com.turnin.shared.domain.core.result.mapError
import com.turnin.shared.domain.feature.announcement.error.AnnouncementErrorType
import com.turnin.shared.domain.feature.announcement.model.AnnouncementId
import com.turnin.shared.domain.feature.announcement.repository.AnnouncementRepository
import kotlin.coroutines.cancellation.CancellationException

/**
 * 공지 읽음 처리
 *
 * @see invoke
 */
class MarkAnnouncementAsReadUseCase(
    private val announcementRepository: AnnouncementRepository,
) {
    /**
     * 공지 읽음 처리를 한다.
     *
     * @param announcementId 공지 ID
     */
    suspend operator fun invoke(announcementId: Long): Result<Unit, AnnouncementErrorType> = try {
        announcementRepository.markAsRead(AnnouncementId(announcementId))
            .mapError { networkError ->
                AnnouncementErrorType.Network(networkError)
            }
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        Result.Error(AnnouncementErrorType.Unexpected(e))
    }
}
