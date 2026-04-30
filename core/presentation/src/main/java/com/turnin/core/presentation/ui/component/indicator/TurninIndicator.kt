package com.turnin.core.presentation.ui.component.indicator

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.turnin.core.designsystem.theme.TurninTheme

/**
 * `PullToRefresh`에서 사용하는 인디케이터
 */
@Composable
fun BoxScope.TurninIndicator(
    isRefreshing: Boolean,
    state: PullToRefreshState,
    modifier: Modifier = Modifier,
) {
    Indicator(
        modifier = modifier.align(Alignment.TopCenter),
        isRefreshing = isRefreshing,
        containerColor = TurninTheme.colorScheme.backgroundNormal,
        color = TurninTheme.colorScheme.primary,
        state = state,
    )
}
