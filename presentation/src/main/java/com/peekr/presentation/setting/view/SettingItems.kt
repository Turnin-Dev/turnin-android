package com.peekr.presentation.setting.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.component.icon.PeekrIcon
import com.peekr.core.designsystem.component.icon.PeekrIconSize
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.click.clickableSingle
import com.peekr.core.designsystem.util.icon.Arrow1Right
import com.peekr.core.designsystem.util.icon.PeekrIcons
import com.peekr.core.designsystem.util.token.ScreenTokens
import com.peekr.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.peekr.presentation.R

/**
 * 설정 항목
 *
 * @property title 항목 타이틀
 * @property onClick 항목 클릭 시 수행할 작업
 */
private data class SettingItem(
    val title: String,
    val onClick: () -> Unit,
)

/**
 * 설정 항목 컨테이너
 *
 * 각 설정 항목의 한 단위이다. (타이틀 + 타이틀에 맞는 항목들 모음)
 *
 * @param modifier [Modifier]
 * @param title 컨테이너 타이틀
 * @param settingItems 설정 항목 리스트
 * @param loading 로딩 여부
 */
@Composable
private fun SettingItemContainer(
    modifier: Modifier = Modifier,
    title: String,
    settingItems: List<SettingItem>,
    loading: Boolean = false,
) {
    Column(modifier) {
        // 항목 타이틀
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ContainerItemTitlePadding),
            text = title,
            style = PeekrTheme.typography.headline4,
            fontWeight = FontWeight.Normal,
            color = PeekrTheme.colorScheme.textAssist2,
            textAlign = TextAlign.Start,
        )
        // 항목 아이템들
        Column(modifier) {
            settingItems.forEach { item ->
                Item(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickableSingle {
                            if (!loading) {
                                item.onClick()
                            }
                        }
                        .padding(ContainerItemPadding),
                    title = item.title,
                    loading = loading,
                )
            }
        }
        // 구분선
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 4.dp,
            color = PeekrTheme.colorScheme.lineDivider.copy(0.5f),
        )
    }
}

/**
 * 설정 항목
 *
 * @param modifier [Modifier]
 * @param title 설정 항목 타이틀
 * @param titleColor 설정 항목 타이틀 색상
 * @param loading 로딩 여부
 */
@Composable
private fun Item(
    modifier: Modifier = Modifier,
    title: String,
    titleColor: Color = PeekrTheme.colorScheme.textNormal,
    loading: Boolean = false,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = PeekrTheme.typography.body3Normal,
            fontWeight = FontWeight.Normal,
            color = titleColor,
        )
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(15.dp),
                strokeCap = StrokeCap.Round,
                strokeWidth = 2.5.dp,
                color = PeekrTheme.colorScheme.primary,
            )
        } else {
            PeekrIcon(
                icon = PeekrIcons.Default.Normal.Arrow1Right,
                iconSize = PeekrIconSize.Small,
                contentDescription = null,
                tint = PeekrTheme.colorScheme.lineNormal,
            )
        }
    }
}

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
            settingItems = listOf(
                SettingItem(
                    title = stringResource(R.string.setting_screen_item_info_account),
                    onClick = onAccountInfoClick,
                ),
            ),
            loading = loading,
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
            settingItems = listOf(
                SettingItem(
                    title = stringResource(R.string.setting_screen_item_notification_setting),
                    onClick = onNotificationSettingClick,
                ),
            ),
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
            settingItems = listOf(
                SettingItem(
                    title = stringResource(R.string.setting_screen_item_account_blocked_list),
                    onClick = onBlockListClick,
                ),
                SettingItem(
                    title = stringResource(R.string.setting_screen_item_account_logout),
                    onClick = onLogoutClick,
                ),
            ),
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
            settingItems = listOf(
                SettingItem(
                    title = stringResource(R.string.setting_screen_item_etc_version),
                    onClick = onVersionClick,
                ),
                SettingItem(
                    title = stringResource(R.string.setting_screen_item_etc_qna),
                    onClick = onQnaClick,
                ),
            ),
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
        Item(
            modifier = Modifier
                .fillMaxWidth()
                .clickableSingle(onClick = onDeleteAccountClick)
                .padding(ContainerItemPadding),
            title = stringResource(R.string.setting_screen_item_delete_account),
            titleColor = PeekrTheme.colorScheme.statusNegative,
        )
    }
}

private val ContainerItemTitlePadding =
    PaddingValues(
        horizontal = ScreenTokens.HorizontalPadding,
        vertical = 12.dp,
    )

private val ContainerItemPadding =
    PaddingValues(
        horizontal = ScreenTokens.HorizontalPadding,
        vertical = 10.dp,
    )

// ------------------------------ Previews ------------------------------
@PreviewLightDarkWithBackground
@Composable
private fun SettingItemContainerPreview() {
    PeekrAppTheme {
        SettingItemContainer(
            modifier = Modifier.fillMaxWidth(),
            title = "정보",
            settingItems = listOf(
                SettingItem("계정 정보 1", {}),
                SettingItem("계정 정보 2", {}),
            ),
        )
    }
}
