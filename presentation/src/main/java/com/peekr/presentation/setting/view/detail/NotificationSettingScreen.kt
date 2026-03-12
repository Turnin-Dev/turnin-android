package com.peekr.presentation.setting.view.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.peekr.core.designsystem.component.switch.PeekrSwitch
import com.peekr.core.designsystem.component.switch.PeekrSwitchSize
import com.peekr.core.designsystem.component.topbar.PeekrTopBar
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.util.token.ScreenTokens
import com.peekr.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.peekr.presentation.R
import com.peekr.presentation.setting.view.SettingItem
import com.peekr.presentation.setting.view.SettingItemContainer

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
    Column(modifier.wrapContentHeight()) {
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
            modifier = modifier.fillMaxWidth(),
            title = stringResource(R.string.setting_detail_notification_setting_title),
            settingItems = {
                SettingItem(
                    title = stringResource(R.string.setting_detail_notification_setting_push_title),
                    description = stringResource(R.string.setting_detail_notification_setting_push_desc),
                    onClick = { togglePush(isPushEnabled) },
                    option = {
                        PeekrSwitch(
                            checked = isPushEnabled,
                            onCheckedChanged = { _ -> togglePush(isPushEnabled) },
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
