package com.peekr.core.domain.setting.model

/** 알림 동기화 상태 */
enum class NotificationSyncState {
    /** 서버에 토큰 등록됨 */
    REGISTERED,

    /** 서버에서 토큰 해제됨 */
    DEACTIVATED,
}
