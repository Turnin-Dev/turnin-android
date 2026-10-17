package com.turnin.shared.feature.announcement.data

import com.turnin.shared.core.network.ApiPath
import com.turnin.shared.core.network.NetworkResult
import com.turnin.shared.core.network.ktorNetworkCall
import com.turnin.shared.core.network.ktorNetworkCallWithoutResponse
import com.turnin.shared.feature.announcement.model.AnnouncementId
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
