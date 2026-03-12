package com.peekr.core.domain.setting.model

/**
 * 앱 설정 값
 *
 * @property pushNotificationEnabled 푸시 알림 활성화 여부
 * @property themeMode 테마 모드
 */
data class AppSetting(
    val pushNotificationEnabled: Boolean,
    val themeMode: ThemeMode,
)
