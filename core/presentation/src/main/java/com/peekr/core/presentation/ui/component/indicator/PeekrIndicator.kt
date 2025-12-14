package com.peekr.core.presentation.ui.component.indicator

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.peekr.core.designsystem.theme.PeekrTheme

/**
 * `PullToRefresh`에서 사용하는 인디케이터
 */
@Composable
fun BoxScope.PeekrIndicator(
    isRefreshing: Boolean,
    state: PullToRefreshState,
    modifier: Modifier = Modifier,
) {
    Indicator(
        modifier = modifier.align(Alignment.TopCenter),
        isRefreshing = isRefreshing,
        containerColor = PeekrTheme.colorScheme.backgroundNormal,
        color = PeekrTheme.colorScheme.primary,
        state = state,
    )
}
