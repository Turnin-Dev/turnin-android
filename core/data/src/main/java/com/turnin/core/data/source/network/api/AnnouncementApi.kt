package com.turnin.core.data.source.network.api

import com.turnin.core.data.source.network.dto.announcement.response.AnnouncementResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * 공지 API
 */
interface AnnouncementApi {
    /**
     * 공지 조회
     */
    @GET(NetworkApiPath.Announcement.ROUTE)
    suspend fun getAnnouncements(): Response<List<AnnouncementResponse>>

    /**
     * 공지 읽음 처리
     */
    @POST(NetworkApiPath.Announcement.READ)
    suspend fun markAsRead(
        @Path("announcementId") announcementId: Long,
    ): Response<Unit>
}
