package com.turnin.core.domain.notification

/**
 * 알림 동기화 매니저 인터페이스
 */
interface NotificationSyncManager {
    /**
     * 모든 알림 상태 변경의 단일 진입점
     *
     * 호출 시점: 앱 시작 / onResume / 알림 토글 / 로그인 / FCM 토큰 갱신 등
     *
     * - 현재 권한 상태와 알림 설정을 읽어 등록/해제 여부를 결정한다.
     * - 연속 호출 시 마지막 호출만 반영되도록 디바운스(300ms)가 적용된다.
     * - ApplicationScope에서 실행되므로 화면 이탈 후에도 완료가 보장된다.
     * - 이미 올바른 상태(REGISTERED / DEACTIVATED)면 API 호출 없이 스킵된다.
     */
    fun sync()
}
