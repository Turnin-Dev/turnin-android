package com.turnin.core.data.source.network.datasource

import com.turnin.core.data.source.network.dto.notification.response.FcmTokenResponse
import com.turnin.core.data.source.network.dto.notification.response.NotificationCursorPageResponse
import com.turnin.core.data.source.network.util.NetworkResult

/**
 * 알림 네트워크 데이터소스
 */
interface NotificationNetworkDataSource {
    /**
     * FCM 토큰 등록
     *
     * @param token FCM 토큰
     */
    suspend fun registerFcmToken(
        token: String,
    ): NetworkResult<FcmTokenResponse>

    /**
     * FCM 토큰 비활성화
     */
    suspend fun deactivateToken(
        token: String,
    ): NetworkResult<Unit>

    /**
     * 알림 목록 조회
     *
     * @param cursor 커서 값
     * @param size 페이지 크기
     */
    suspend fun getNotifications(
        cursor: Long?,
        size: Int,
    ): NetworkResult<NotificationCursorPageResponse>

    /**
     * 알림 읽음 처리
     *
     * @param notificationId 알림 ID
     */
    suspend fun markAsRead(
        notificationId: Long,
    ): NetworkResult<Unit>
}
