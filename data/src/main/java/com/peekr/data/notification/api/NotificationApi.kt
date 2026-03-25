package com.peekr.data.notification.api

import com.peekr.core.data.source.network.api.NetworkApiPath
import com.peekr.data.notification.dto.FcmTokenResponse
import com.peekr.data.notification.dto.NotificationCursorPageResponse
import com.peekr.data.notification.dto.RegisterFcmTokenRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationApi {
    /**
     * FCM 토큰 등록
     */
    @POST(NetworkApiPath.Notification.TOKEN)
    suspend fun registerFcmToken(
        @Body request: RegisterFcmTokenRequest,
    ): Response<FcmTokenResponse>

    /**
     * 알림 목록 조회
     */
    @GET(NetworkApiPath.Notification.ROUTE)
    suspend fun getNotifications(
        @Query("cursor") cursor: Long?,
        @Query("size") size: Int,
    ): Response<NotificationCursorPageResponse>

    /**
     * 알림 읽음 처리
     */
    @PATCH(NetworkApiPath.Notification.READ)
    suspend fun markAsRead(
        @Path("notificationId") notificationId: Long,
    ): Response<Unit>
}
