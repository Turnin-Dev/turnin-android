package com.peekr.core.domain.notification

/**
 * 알림 동기화 매니저 인터페이스
 */
interface NotificationSyncManager {
    /**
     * 모든 알림 상태 변경의 단일 진입점
     * - 앱 시작 / onResume / 토글 변경 / 로그인 / onNewToken 모두 여기로
     */
    suspend fun sync()
}
