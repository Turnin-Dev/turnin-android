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
                pushNotificationEnabled = true,
                themeMode = ThemeMode.SYSTEM,
            ),
        )

    // 토글 시 긴 작업 처리 방식
    // 1. 단순 로컬 설정 변경만 하는 경우: withContext(NonCancellable) 정도로 충분. 단, 유의해야 함.
    // 2. 서버 API 호출하는 경우: @ApplicationScope를 주입받아 사용.
    // 3. 네트워크가 불안정해도 무조건 성공해야하는 경우: WorkManager 사용.
    fun togglePushNotification(enabled: Boolean) {
        viewModelScope.launch {
            runCatching { settingRepository.setPushNotificationEnabled(enabled) }

            // TODO: 실제로 서버에서도 FCM 토큰을 제거하는 작업을 수행해야 한다.
        }
    }
}
