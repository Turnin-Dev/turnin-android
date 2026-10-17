package com.turnin.shared.feature.announcement.data

import com.turnin.shared.core.network.NetworkResult
import com.turnin.shared.feature.announcement.model.AnnouncementId

/**
 * 공지 네트워크 데이터 소스
 */
internal interface AnnouncementNetworkDataSource {
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
