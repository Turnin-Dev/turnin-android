package com.turnin.core.data.source.network.datasource

import com.turnin.core.data.source.network.dto.announcement.response.AnnouncementResponse
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.domain.model.AnnouncementId

/**
 * 공지 네트워크 데이터 소스
 */
interface AnnouncementNetworkDataSource {
    /**
     * 공지 조회
     */
    suspend fun getAnnouncements(): NetworkResult<List<AnnouncementResponse>>

    /**
     * 공지 읽음 처리
     *
     * @param announcementId 공지 ID
     */
    suspend fun markAsRead(announcementId: AnnouncementId): NetworkResult<Unit>
}
