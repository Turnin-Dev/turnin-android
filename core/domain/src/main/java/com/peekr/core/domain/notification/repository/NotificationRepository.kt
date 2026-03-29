package com.peekr.core.domain.notification.repository

import androidx.paging.PagingData
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.model.NotificationId
import com.peekr.core.domain.notification.model.Notification
import com.peekr.core.domain.setting.model.NotificationSyncState
import kotlinx.coroutines.flow.Flow

/** Notification 리포지토리 */
interface NotificationRepository {
    /**
     * FCM 토큰 조회
     */
    suspend fun getFcmToken(): String?

    /**
     * 구독된 토픽을 구독 해제한다.
     */
    suspend fun unsubscribeFromTopic()

    /**
     * 토픽을 구독한다.
     */
    suspend fun subscribeToTopic()

    /**
     * FCM 토큰 등록
     */
    suspend fun registerFcmToken(token: String): Result<Unit, CommonErrorType>

    /**
     * FCM 토큰 비활성화
     */
    suspend fun deactivateFcmToken(token: String): Result<Unit, CommonErrorType>

    /**
     * 알림 목록 조회 (커서 기반 페이지네이션)
     */
    fun getNotifications(): Flow<PagingData<Notification>>

    /**
     * 알림 읽음 처리
     */
    suspend fun markAsRead(notificationId: NotificationId): Result<Unit, CommonErrorType>

    /**
     * 알림 동기화 상태 조회
     *
     * @return 알림 동기화 상태 [NotificationSyncState]
     */
    suspend fun getNotificationSyncState(): NotificationSyncState?

    /**
     * 알림 동기화 상태 설정
     *
     * @param state 알림 동기화 상태 [NotificationSyncState]
     */
    suspend fun setNotificationSyncState(state: NotificationSyncState)
}
