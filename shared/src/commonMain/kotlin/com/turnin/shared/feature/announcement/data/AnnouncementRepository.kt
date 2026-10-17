package com.turnin.shared.feature.announcement.data

import com.turnin.shared.core.error.NetworkErrorType
import com.turnin.shared.core.result.Result
import com.turnin.shared.feature.announcement.model.Announcement
import com.turnin.shared.feature.announcement.model.AnnouncementId
import kotlinx.coroutines.flow.Flow

/**
 * 공지 리포지토리
 */
internal interface AnnouncementRepository {
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
