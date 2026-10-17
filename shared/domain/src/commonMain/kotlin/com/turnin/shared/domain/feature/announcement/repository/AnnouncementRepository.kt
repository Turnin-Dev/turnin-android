package com.turnin.shared.domain.feature.announcement.repository

import com.turnin.shared.domain.core.error.NetworkErrorType
import com.turnin.shared.domain.core.result.Result
import com.turnin.shared.domain.feature.announcement.model.Announcement
import com.turnin.shared.domain.feature.announcement.model.AnnouncementId
import kotlinx.coroutines.flow.Flow

/**
 * 공지 리포지토리
 */
interface AnnouncementRepository {
    /**
     * 공지 목록 조회
     */
    fun getAnnouncements(): Flow<Result<List<Announcement>, NetworkErrorType>>

    /**
     * 공지 읽음 처리
     *
     * @param announcementId 공지 ID
     */
    suspend fun markAsRead(announcementId: AnnouncementId): Result<Unit, NetworkErrorType>
}
