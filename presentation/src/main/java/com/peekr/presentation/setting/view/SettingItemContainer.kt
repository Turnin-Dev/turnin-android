package com.peekr.presentation.setting.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
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
internal fun SettingItemContainer(
    modifier: Modifier = Modifier,
    title: String,
    loading: Boolean = false,
    settingItems: @Composable ColumnScope.() -> Unit,
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
            settingItems()
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
 * @param title 제목
 * @param onClick 클릭 시 콜백
 * @param modifier [Modifier]
 * @param titleColor 제목 색상
 * @param description 설명
 * @param descriptionColor 설명 색상
 * @param loading 로딩 여부
 * @param option 우측 옵션
 */
@Composable
internal fun SettingItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    titleColor: Color = PeekrTheme.colorScheme.textNormal,
    description: String? = null,
    descriptionColor: Color = PeekrTheme.colorScheme.textAssist2,
    loading: Boolean = false,
    option: @Composable () -> Unit = {
        PeekrIcon(
            icon = PeekrIcons.Default.Normal.Arrow1Right,
            iconSize = PeekrIconSize.Small,
            contentDescription = null,
            tint = PeekrTheme.colorScheme.lineNormal,
        )
    },
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = ItemMinHeightDp)
            .clickableSingle(onClick = onClick)
            .padding(ContainerItemPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            // 제목
            Text(
                text = title,
                style = PeekrTheme.typography.body3Normal,
                fontWeight = FontWeight.Normal,
                color = titleColor,
            )

            // 설명
            description?.let {
                Text(
                    text = description,
                    style = PeekrTheme.typography.body5,
                    fontWeight = FontWeight.Normal,
                    color = descriptionColor,
                )
            }
        }

        // 우측 옵션 & 로딩
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(15.dp),
                strokeCap = StrokeCap.Round,
                strokeWidth = 2.5.dp,
                color = PeekrTheme.colorScheme.primary,
            )
        } else {
            option()
        }
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

private val ItemMinHeightDp = 57.dp

// ------------------------------ Previews ------------------------------
@PreviewLightDarkWithBackground
@Composable
private fun SettingSettingItemContainerPreview() {
    PeekrAppTheme {
        SettingItemContainer(
            modifier = Modifier.fillMaxWidth(),
            title = "정보",
            settingItems = {
                SettingItem(
                    title = "계정 정보 1",
                    onClick = {},
                )
                SettingItem(
                    title = "계정 정보 2",
                    onClick = {},
                )
            },
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun SettingSettingItemContainerPreview2() {
    PeekrAppTheme {
        SettingItemContainer(
            modifier = Modifier.fillMaxWidth(),
            title = "정보",
            settingItems = {
                SettingItem(
                    title = "계정 정보 1",
                    onClick = {},
                )
                SettingItem(
                    title = "계정 정보 2",
                    description = "계정 정보 보조 타이틀",
                    onClick = {},
                )
            },
        )
    }
}
