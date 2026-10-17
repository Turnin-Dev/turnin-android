package com.turnin.shared.feature.announcement.usecase

import com.turnin.shared.core.result.Result
import com.turnin.shared.core.result.mapError
import com.turnin.shared.feature.announcement.data.AnnouncementRepository
import com.turnin.shared.feature.announcement.error.AnnouncementErrorType
import com.turnin.shared.feature.announcement.model.AnnouncementId
import kotlin.coroutines.cancellation.CancellationException

/**
 * 공지 읽음 처리
 *
 * @see invoke
 */
class MarkAnnouncementAsReadUseCase internal constructor(
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
