package com.turnin.core.designsystem.component.tabBar

import androidx.compose.foundation.pager.PagerScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.turnin.core.designsystem.theme.TurninTheme

/**
 * Turnin TabBar
 *
 * @param modifier [Modifier]
 * @param tabs 탭 타이틀 리스트
 * @param pageContent [PagerScope] 범위의 페이지 컨텐츠
 * @param containerColor 컨테이너 색상
 * @param contentColor 컨텐츠 색상
 * @param userScrollEnabled 사용자 스크롤 여부
 *
 * @see CoreTabBar
 * @sample TurninTabBarPreview
 */
@Composable
fun TurninTabBar(
    modifier: Modifier = Modifier,
    tabs: List<String>,
    pageContent: @Composable (PagerScope.(Int) -> Unit),
    containerColor: Color = TurninTheme.colorScheme.backgroundNormal,
    contentColor: Color = TurninTheme.colorScheme.textNormal,
    userScrollEnabled: Boolean = true,
) {
    CoreTabBar(
        modifier = modifier,
        tabs = tabs,
        pageContent = pageContent,
        containerColor = containerColor,
        contentColor = contentColor,
        userScrollEnabled = userScrollEnabled,
    )
}
