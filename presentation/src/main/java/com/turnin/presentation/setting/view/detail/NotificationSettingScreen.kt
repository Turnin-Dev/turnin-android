package com.turnin.presentation.setting.view.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.turnin.core.designsystem.component.switch.PeekrSwitch
import com.turnin.core.designsystem.component.switch.PeekrSwitchSize
import com.turnin.core.designsystem.component.topbar.PeekrTopBar
import com.turnin.core.designsystem.theme.PeekrAppTheme
import com.turnin.core.designsystem.util.token.ScreenTokens
import com.turnin.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.turnin.presentation.R
import com.turnin.presentation.setting.view.SettingItem
import com.turnin.presentation.setting.view.SettingItemContainer

/**
 * 알림 설정 화면
 *
 * @param modifier [Modifier]
 * @param isPushEnabled 푸시 알림 활성화 여부
 * @param togglePush 푸시 알림 활성화 토글
 */
@Composable
fun NotificationSettingScreen(
    modifier: Modifier = Modifier,
    isPushEnabled: Boolean,
    togglePush: (Boolean) -> Unit,
    onBackPressed: () -> Unit,
) {
    Column(modifier) {
        // 탑바
        PeekrTopBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ScreenTokens.HorizontalPaddingWithTouchTarget),
            title = stringResource(R.string.setting_detail_notification_setting_top_bar),
            onBackPressed = onBackPressed,
        )

        // 일반 항목
        SettingItemContainer(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(R.string.setting_detail_notification_setting_title),
            enableDivider = false,
            settingItems = {
                SettingItem(
                    title = stringResource(R.string.setting_detail_notification_setting_push_title),
                    description = stringResource(R.string.setting_detail_notification_setting_push_desc),
                    onClick = { togglePush(!isPushEnabled) },
                    option = {
                        PeekrSwitch(
                            checked = isPushEnabled,
                            onCheckedChanged = { checked -> togglePush(checked) },
                            size = PeekrSwitchSize.Small,
                        )
                    },
                )
            },
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun NotificationSettingScreenPreview() {
    var checked by remember { mutableStateOf(false) }

    PeekrAppTheme {
        NotificationSettingScreen(
            modifier = Modifier.fillMaxSize(),
            isPushEnabled = checked,
            togglePush = { checked = !checked },
            onBackPressed = {},
        )
    }
}
