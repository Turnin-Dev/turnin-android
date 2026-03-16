package com.peekr.core.data.repository

import com.peekr.core.data.source.local.datastore.DataStoreKey
import com.peekr.core.data.source.local.datastore.DataStoreManager
import com.peekr.core.domain.setting.model.AppSetting
import com.peekr.core.domain.setting.model.ThemeMode
import com.peekr.core.domain.setting.repository.SettingRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class SettingRepositoryImpl @Inject constructor(
    private val dataStoreManager: DataStoreManager,
) : SettingRepository {
    override val appSetting: Flow<AppSetting> =
        combine(
            dataStoreManager.getBooleanData(DataStoreKey.Setting.PushNotification),
            dataStoreManager.getStringData(DataStoreKey.Setting.ThemeMode),
        ) { pushNotification, themeMode ->
            AppSetting(
                pushNotificationEnabled = pushNotification ?: true,
                themeMode = themeMode?.let {
                    ThemeMode.find(themeMode)
                } ?: ThemeMode.SYSTEM,
            )
        }

    override suspend fun setPushNotificationEnabled(enabled: Boolean) {
        dataStoreManager.saveBooleanData(
            key = DataStoreKey.Setting.PushNotification,
            value = enabled,
        )
    }

    override suspend fun setThemeMode(themeMode: ThemeMode) {
        dataStoreManager.saveStringData(
            key = DataStoreKey.Setting.ThemeMode,
            value = themeMode.name,
        )
    }
}
