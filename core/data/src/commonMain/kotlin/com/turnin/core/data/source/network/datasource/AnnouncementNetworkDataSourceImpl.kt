package com.turnin.core.data.source.network.datasource

import com.turnin.core.data.source.network.api.NetworkApiPath
import com.turnin.core.data.source.network.dto.announcement.response.AnnouncementResponse
import com.turnin.core.data.source.network.ktor.ktorNetworkCall
import com.turnin.core.data.source.network.ktor.ktorNetworkCallWithoutResponse
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.domain.model.AnnouncementId
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post

/**
 * 공지 네트워크 데이터 소스 (Ktor)
 *
 * @param httpClient [com.turnin.core.data.source.network.ktor.createHttpClient]로 생성한 클라이언트
 */
class AnnouncementNetworkDataSourceImpl(
    private val httpClient: HttpClient,
) : AnnouncementNetworkDataSource {
    override suspend fun getAnnouncements(): NetworkResult<List<AnnouncementResponse>> =
        ktorNetworkCall { httpClient.get(NetworkApiPath.Announcement.ROUTE) }

    override suspend fun markAsRead(announcementId: AnnouncementId): NetworkResult<Unit> =
        ktorNetworkCallWithoutResponse {
            httpClient.post(
                NetworkApiPath.Announcement.READ.replace("{announcementId}", announcementId.value.toString()),
            )
        }
}
