package com.turnin.core.domain.announcement.repository

import com.turnin.core.domain.announcement.model.Announcement
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.model.AnnouncementId
import kotlinx.coroutines.flow.Flow

/**
 * 공지 리포지토리
 */
interface AnnouncementRepository {
    /**
     * 공지 목록 조회
     */
    fun getAnnouncements(): Flow<Result<List<Announcement>, CommonErrorType>>

    /**
     * 공지 읽음 처리
     *
     * @param announcementId 공지 ID
     */
    suspend fun markAsRead(announcementId: AnnouncementId): Result<Unit, CommonErrorType>
}
