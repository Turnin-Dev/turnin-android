package com.peekr.presentation.setting.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
) : ViewModel() {
    val appSetting = settingRepository.appSetting
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppSetting(
                pushNotificationEnabled = false,
                themeMode = ThemeMode.SYSTEM,
            ),
        )

    fun togglePushNotification(enabled: Boolean) {
        viewModelScope.launch {
            settingRepository.setPushNotificationEnabled(!enabled)

            // TODO: 실제로 서버에서도 FCM 토큰을 제거하는 작업을 수행해야 한다.
        }
    }
}
