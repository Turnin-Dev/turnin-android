package com.peekr.peekrapp.util.notification

import com.peekr.core.common.coroutine.ApplicationScope
import com.peekr.core.common.logger.AppLogger
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.notification.NotificationSyncManager
import com.peekr.core.domain.notification.repository.NotificationRepository
import com.peekr.core.domain.setting.model.NotificationSyncState
import com.peekr.core.domain.setting.repository.SettingRepository
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * 알림 동기화 매니저 클래스
 */
@OptIn(FlowPreview::class)
class NotificationSyncManagerImpl @Inject constructor(
    private val notificationPermissionManager: NotificationPermissionManager,
    private val notificationRepository: NotificationRepository,
    private val settingRepository: SettingRepository,
    @param:ApplicationScope private val applicationScope: CoroutineScope,
) : NotificationSyncManager {
    private val tag = this::class.java.simpleName

    private val pendingSync = AtomicReference<Job?>(null)

    private val mutex = Mutex()

    /**
     * 모든 알림 상태 변경의 단일 진입점
     * - 앱 시작 / onResume / 토글 변경 / 로그인 / onNewToken 모두 여기로
     */
    override fun sync() {
        pendingSync.getAndSet(
            applicationScope.launch {
                delay(300) // 디바운스
                mutex.withLock {
                    doSync()
                }
            },
        )?.cancel()
    }

    private suspend fun doSync() {
        val hasPermission = notificationPermissionManager.hasPermission()
        val isEnabled = settingRepository.appSetting.first().pushNotificationEnabled
        val shouldRegister = hasPermission && isEnabled
        val lastState = notificationRepository.getNotificationSyncState()

        when {
            // 등록 필요 & 이미 등록됨 / 등록 불필요 & 이미 해제됨 → 변경 없음
            (shouldRegister && lastState == NotificationSyncState.REGISTERED) ||
                (!shouldRegister && lastState == NotificationSyncState.DEACTIVATED) -> {
                AppLogger.d(tag, "FCM 상태 변경 없음")
            }
            // 권한 있음 & 알림 활성화 → 토큰 등록
            shouldRegister -> registerTokenAndSubscribe()
            // 권한 없음 or 알림 비활성화(null 상태 포함) → 토큰 해제
            else -> unsubscribe()
        }
    }

    // FCM 토큰 등록
    private suspend fun registerTokenAndSubscribe() {
        val token = getFcmToken() ?: return
        when (val result = notificationRepository.registerFcmToken(token)) {
            is Result.Success -> {
                notificationRepository.setNotificationSyncState(NotificationSyncState.REGISTERED)
                notificationRepository.subscribeToTopic()
                AppLogger.d(tag, "FCM 토큰 등록 성공")
            }

            is Result.Error -> AppLogger.e(tag, "FCM 토큰 등록 실패: ${result.message}")
            else -> Unit
        }
    }

    // FCM 토큰 등록 해제
    private suspend fun unsubscribe() {
        val state = notificationRepository.getNotificationSyncState()
        if (state == NotificationSyncState.DEACTIVATED || state == null) {
            AppLogger.d(tag, "FCM 토큰 미등록 상태 - deactivate 스킵")
            return
        }
        val token = getFcmToken() ?: return
        when (val result = notificationRepository.deactivateFcmToken(token)) {
            is Result.Success -> {
                notificationRepository.setNotificationSyncState(NotificationSyncState.DEACTIVATED)
                notificationRepository.unsubscribeFromTopic()
                AppLogger.d(tag, "FCM 토큰 비활성화 성공")
            }

            is Result.Error -> AppLogger.e(tag, "FCM 토큰 비활성화 실패: ${result.message}")
            else -> Unit
        }
    }

    // FCM 토큰 조회
    private suspend fun getFcmToken(): String? {
        val token = notificationRepository.getFcmToken()
        if (token == null) AppLogger.e(tag, "FCM 토큰 발급 실패")
        return token
    }
}
