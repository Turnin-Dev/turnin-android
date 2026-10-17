package com.turnin.shared.data.feature.announcement.datasource

import com.turnin.shared.data.core.network.ApiPath
import com.turnin.shared.data.core.network.NetworkResult
import com.turnin.shared.data.core.network.ktorNetworkCall
import com.turnin.shared.data.core.network.ktorNetworkCallWithoutResponse
import com.turnin.shared.data.feature.announcement.dto.AnnouncementResponse
import com.turnin.shared.domain.feature.announcement.model.AnnouncementId
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post

/**
 * 공지 네트워크 데이터 소스 (Ktor)
 *
 * @param httpClient [com.turnin.shared.core.network.createHttpClient]로 생성한 클라이언트
 */
internal class AnnouncementNetworkDataSourceImpl(
    private val httpClient: HttpClient,
) : AnnouncementNetworkDataSource {
    override suspend fun getAnnouncements(): NetworkResult<List<AnnouncementResponse>> =
        ktorNetworkCall { httpClient.get(ApiPath.Announcement.ROUTE) }

    override suspend fun markAsRead(announcementId: AnnouncementId): NetworkResult<Unit> =
        ktorNetworkCallWithoutResponse {
            httpClient.post(
                ApiPath.Announcement.READ.replace("{announcementId}", announcementId.value.toString()),
            )
        }
}
