package com.turnin.core.data.source.network.datasource

import com.turnin.core.data.source.network.api.AnnouncementApi
import com.turnin.core.data.source.network.dto.announcement.response.AnnouncementResponse
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.data.source.network.util.networkCall
import com.turnin.core.data.source.network.util.networkCallWithoutResponse
import com.turnin.core.domain.model.AnnouncementId
import javax.inject.Inject

class AnnouncementNetworkDataSourceImpl @Inject constructor(
    private val announcementApi: AnnouncementApi,
) : AnnouncementNetworkDataSource {
    override suspend fun getAnnouncements(): NetworkResult<List<AnnouncementResponse>> =
        networkCall { announcementApi.getAnnouncements() }

    override suspend fun markAsRead(announcementId: AnnouncementId): NetworkResult<Unit> =
        networkCallWithoutResponse { announcementApi.markAsRead(announcementId.value) }
}
