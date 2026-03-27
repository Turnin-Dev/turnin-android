package com.peekr.presentation.setting.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peekr.core.domain.notification.NotificationSyncManager
import com.peekr.core.domain.setting.model.AppSetting
import com.peekr.core.domain.setting.model.ThemeMode
import com.peekr.core.domain.setting.repository.SettingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class NotificationSettingViewModel @Inject constructor(
    private val settingRepository: SettingRepository,
    private val notificationSyncManager: NotificationSyncManager,
) : ViewModel() {
    val appSetting = settingRepository.appSetting
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppSetting(
                pushNotificationEnabled = true,
                themeMode = ThemeMode.SYSTEM,
            ),
        )

    fun togglePushNotificationAndSync(enabled: Boolean) {
        viewModelScope.launch {
            runCatching { settingRepository.setPushNotificationEnabled(enabled) }
            runCatching { notificationSyncManager.sync() }
        }
    }
}
