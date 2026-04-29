package com.turnin.presentation.setting.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.turnin.core.designsystem.theme.PeekrTheme
import com.turnin.presentation.R

/**
 * 정보 항목
 *
 * @param loading 로딩 여부
 * @param onAccountInfoClick 계정 정보 클릭 시 콜백
 */
fun LazyListScope.informationItem(
    loading: Boolean = false,
    onAccountInfoClick: () -> Unit,
) {
    item {
        SettingItemContainer(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(R.string.setting_screen_item_info_title),
            settingItems = {
                SettingItem(
                    title = stringResource(R.string.setting_screen_item_info_account),
                    onClick = onAccountInfoClick,
                    loading = loading,
                )
            },
        )
    }
}

/**
 * 알림 항목
 *
 * @param onNotificationSettingClick 알림 설정 클릭 시 콜백
 */
fun LazyListScope.notificationItem(
    onNotificationSettingClick: () -> Unit,
) {
    item {
        SettingItemContainer(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(R.string.setting_screen_item_notification_title),
            settingItems = {
                SettingItem(
                    title = stringResource(R.string.setting_screen_item_notification_setting),
                    onClick = onNotificationSettingClick,
                )
            },
        )
    }
}

/**
 * 계정 항목
 *
 * @param onBlockListClick 차단 사용자 관리 클릭 시 콜백
 * @param onLogoutClick 로그아웃 클릭 시 콜백
 */
fun LazyListScope.accountItem(
    onBlockListClick: () -> Unit,
    onLogoutClick: () -> Unit,
) {
    item {
        SettingItemContainer(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(R.string.setting_screen_item_account_title),
            settingItems = {
                SettingItem(
                    title = stringResource(R.string.setting_screen_item_account_blocked_list),
                    onClick = onBlockListClick,
                )
                SettingItem(
                    title = stringResource(R.string.setting_screen_item_account_logout),
                    onClick = onLogoutClick,
                )
            },
        )
    }
}

/**
 * 기타 항목
 *
 * @param onVersionClick 버전 정보 클릭 시 콜백
 * @param onQnaClick 문의 클릭 시 콜백
 */
fun LazyListScope.etcItem(
    onVersionClick: () -> Unit,
    onQnaClick: () -> Unit,
) {
    item {
        SettingItemContainer(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(R.string.setting_screen_item_etc_title),
            settingItems = {
                SettingItem(
                    title = stringResource(R.string.setting_screen_item_etc_version),
                    onClick = onVersionClick,
                )
                SettingItem(
                    title = stringResource(R.string.setting_screen_item_etc_qna),
                    onClick = onQnaClick,
                )
            },
        )
    }
}

/**
 * 계정 삭제 항목
 *
 * @param onDeleteAccountClick 계정 삭제 클릭 시 콜백
 */
fun LazyListScope.deleteAccountItem(
    onDeleteAccountClick: () -> Unit,
) {
    item {
        SettingItem(
            title = stringResource(R.string.setting_screen_item_delete_account),
            titleColor = PeekrTheme.colorScheme.statusNegative,
            onClick = onDeleteAccountClick,
        )
    }
}
