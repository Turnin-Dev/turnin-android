package com.peekr.designsystem.component.tabBar

import androidx.compose.foundation.pager.PagerScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.peekr.designsystem.theme.PeekrTheme

@Composable
fun PeekrTabBar(
    tabs: List<String>,
    pageContent: @Composable (PagerScope.(Int) -> Unit),
    containerColor: Color = PeekrTheme.colorScheme.backgroundNormal,
    contentColor: Color = PeekrTheme.colorScheme.textNormal,
    modifier: Modifier = Modifier,
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
