package com.turnin.presentation.setting.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.turnin.core.designsystem.component.topbar.TurninTopBar
import com.turnin.core.designsystem.theme.TurninAppTheme
import com.turnin.core.designsystem.util.token.ScreenTokens
import com.turnin.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.turnin.presentation.R
import com.turnin.presentation.setting.state.SettingContract

/**
 * 설정 화면 프레임
 *
 * @param modifier [Modifier]
 * @param topBar 탑바
 * @param settings 설정 항목 들
 */
@Composable
private fun SettingScreenFrame(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit,
    settings: LazyListScope.() -> Unit,
) {
    Column(modifier) {
        topBar()
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 60.dp),
        ) {
            settings()
        }
    }
}

/**
 * 설정 화면
 *
 * @param modifier [Modifier]
 * @param accountInfoLoading 계정 정보 로딩 여부
 * @param onUiEvent 설정 UI 이벤트
 * @param onBackPressed 뒤로 가기 시 콜백
 */
@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    accountInfoLoading: Boolean,
    onUiEvent: (SettingContract.UiEvent) -> Unit,
    onBackPressed: () -> Unit,
) {
    SettingScreenFrame(
        modifier = modifier,
        topBar = {
            TopBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ScreenTokens.HorizontalPaddingWithTouchTarget),
                onBackPressed = onBackPressed,
            )
        },
        settings = {
            // 정보 항목
            informationItem(
                loading = accountInfoLoading,
                onAccountInfoClick = {
                    onUiEvent(SettingContract.UiEvent.OnNavigateToAccountInfo)
                },
            )
            // 알림 항목
            notificationItem(
                onNotificationSettingClick = {
                    onUiEvent(SettingContract.UiEvent.OnNavigateToNotification)
                },
            )
            // 계정 항목
            accountItem(
                onBlockListClick = {
                    onUiEvent(SettingContract.UiEvent.OnNavigateToBlockList)
                },
                onLogoutClick = {
                    onUiEvent(SettingContract.UiEvent.OnLogoutClick)
                },
            )
            // 기타 항목
            etcItem(
                onVersionClick = {
                    onUiEvent(SettingContract.UiEvent.OnNavigateToVersionInfo)
                },
                onQnaClick = {
                    onUiEvent(SettingContract.UiEvent.OnNavigateToQna)
                },
            )
            // 계정 삭제 항목
            deleteAccountItem(
                onDeleteAccountClick = {
                    onUiEvent(SettingContract.UiEvent.OnDeleteAccountClick)
                },
            )
        },
    )
}

/**
 * 탑바
 *
 * @param modifier [Modifier]
 * @param onBackPressed 뒤로 가기 시 콜백
 */
@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
) {
    TurninTopBar(
        modifier = modifier,
        title = stringResource(R.string.setting_screen_top_bar_title),
        onBackPressed = onBackPressed,
    )
}

// ------------------------------ Previews ------------------------------
@PreviewLightDarkWithBackground
@Composable
private fun SettingScreenPreview() {
    TurninAppTheme {
        SettingScreen(
            modifier = Modifier.fillMaxSize(),
            accountInfoLoading = true,
            onUiEvent = {},
            onBackPressed = {},
        )
    }
}
