package com.peekr.peekrapp.util.notification

import com.peekr.core.common.coroutine.ApplicationScope
import com.peekr.core.common.logger.AppLogger
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.notification.repository.NotificationRepository
import com.peekr.core.domain.setting.repository.SettingRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

/**
 * 알림 동기화 매니저 클래스
 */
@Singleton
class NotificationSyncManager @Inject constructor(
    private val notificationPermissionManager: NotificationPermissionManager,
    private val notificationRepository: NotificationRepository,
    private val settingRepository: SettingRepository,
    @param:ApplicationScope private val applicationScope: CoroutineScope,
) {
    private val tag = this::class.java.simpleName

    init {
        settingRepository.appSetting
            .map { it.pushNotificationEnabled }
            .distinctUntilChanged()
            .drop(1)
            .onEach { sync() }
            .launchIn(applicationScope)
    }

    /**
     * 모든 알림 상태 변경의 단일 진입점
     * - 앱 시작 / onResume / 토글 변경 / 로그인 / onNewToken 모두 여기로
     */
    suspend fun sync() {
        val hasPermission = notificationPermissionManager.hasPermission()
        val isEnabled = settingRepository.appSetting.first().pushNotificationEnabled

        if (hasPermission && isEnabled) {
            registerTokenAndSubscribe()
        } else {
            unsubscribe()
        }
    }

    private suspend fun registerTokenAndSubscribe() {
        val token = notificationRepository.getFcmToken()
        if (token == null) {
            AppLogger.e(tag, "FCM 토큰 발급 실패")
            return
        }

        when (val result = notificationRepository.registerFcmToken(token)) {
            is Result.Success -> {
                AppLogger.d(tag, "FCM 토큰 등록 성공")
                notificationRepository.subscribeToTopic()
            }

            is Result.Error -> AppLogger.e(tag, "FCM 토큰 등록 실패: ${result.message}")
            else -> Unit
        }
    }

    private suspend fun unsubscribe() {
        notificationRepository.unsubscribeFromTopic()
        // TODO: FCM 토큰 비활성화 API 호출
    }
}
