package com.peekr.core.domain.setting.repository

import com.peekr.core.domain.setting.model.AppSetting
import com.peekr.core.domain.setting.model.ThemeMode
import kotlinx.coroutines.flow.Flow

/** 설정 리포지토리 */
interface SettingRepository {
    /**
     * 앱 설정 값
     */
    val appSetting: Flow<AppSetting>

    /**
     * 푸시 알림 활성화 값 설정
     */
    suspend fun setPushNotificationEnabled(enabled: Boolean)

    /**
     * 테마 모드 설정
     *
     * @param themeMode 테마 모드 [ThemeMode]
     */
    suspend fun setThemeMode(themeMode: ThemeMode)
}
