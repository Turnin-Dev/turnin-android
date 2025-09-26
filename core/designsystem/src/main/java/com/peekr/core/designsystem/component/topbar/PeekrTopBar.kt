package com.peekr.core.designsystem.component.topbar

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PeekrTopBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    onBackPressed: (() -> Unit)? = null,
    optionSlot: @Composable (RowScope.() -> Unit)? = null,
) {
    CoreTopBar(
        modifier = modifier.padding(PaddingValues),
        onBackPressed = onBackPressed,
        title = title,
        optionSlot = optionSlot,
    )
}

// 탑바 패딩
private val PaddingValues = PaddingValues(horizontal = 10.dp)
