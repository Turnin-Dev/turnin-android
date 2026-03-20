package com.peekr.domain.notification.repository

import androidx.paging.PagingData
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.model.NotificationId
import com.peekr.domain.notification.error.NotificationErrorType
import com.peekr.domain.notification.model.Notification
import kotlinx.coroutines.flow.Flow

/** Notification 리포지토리 */
interface NotificationRepository {
    /**
     * FCM 토큰 등록
     */
    suspend fun registerFcmToken(token: String): Result<Unit, NotificationErrorType>

    /**
     * FCM 토큰 비활성화
     */
    suspend fun deactivateFcmToken(token: String): Result<Unit, NotificationErrorType>

    /**
     * 알림 목록 조회 (커서 기반 페이지네이션)
     */
    fun getNotifications(): Flow<PagingData<Notification>>

    /**
     * 알림 읽음 처리
     */
    fun markAsRead(notificationId: NotificationId): Flow<Result<Unit, NotificationErrorType>>
}
