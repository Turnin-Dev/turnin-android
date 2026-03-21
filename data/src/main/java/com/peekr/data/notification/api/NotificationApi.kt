package com.peekr.data.notification.api

import com.peekr.data.notification.dto.FcmTokenResponse
import com.peekr.data.notification.dto.NotificationCursorPageResponse
import com.peekr.data.notification.dto.RegisterFcmTokenRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationApi {
    /**
     * FCM 토큰 등록
     */
    @POST("notification/token")
    suspend fun registerFcmToken(
        @Body request: RegisterFcmTokenRequest,
    ): Response<FcmTokenResponse>

    /**
     * FCM 토큰 비활성화
     */
    @DELETE("notification/token")
    suspend fun deactivateFcmToken(
        @Query("token") token: String,
    ): Response<Unit>

    /**
     * 알림 목록 조회
     */
    @GET("notification")
    suspend fun getNotifications(
        @Query("cursor") cursor: Long?,
        @Query("size") size: Int,
    ): Response<NotificationCursorPageResponse>

    /**
     * 알림 읽음 처리
     */
    @PATCH("notification/{notificationId}/read")
    suspend fun markAsRead(
        @Path("notificationId") notificationId: Long,
    ): Response<Unit>
}
