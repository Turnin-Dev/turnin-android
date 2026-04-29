package com.turnin.core.presentation.ui.component.lazycolumn

import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.turnin.core.presentation.ui.component.indicator.TurninIndicator

/**
 * 새로고침 기능이 포함된 [LazyColumn]
 *
 * [PullToRefreshBox]와 [LazyColumn]를 통합
 *
 * @param modifier [Modifier]
 * @param isRefreshing 새로고침 로딩 여부
 * @param onRefresh 새로고침 람다
 * @param indicator 새로고침 인디케이터
 * @param state [LazyColumn] state ([LazyListState])
 * @param contentPadding [LazyColumn] contentPadding ([PaddingValues])
 * @param reverseLayout [LazyColumn] reverseLayout
 * @param verticalArrangement [LazyColumn] verticalArrangement
 * @param horizontalAlignment [LazyColumn] horizontalAlignment
 * @param flingBehavior [LazyColumn] flingBehavior
 * @param userScrollEnabled [LazyColumn] userScrollEnabled
 * @param overscrollEffect [LazyColumn] overscrollEffect
 * @param content [LazyColumn] content
 *
 * @see PullToRefreshBox
 * @see LazyColumn
 */
@Composable
fun RefreshableLazyColumn(
    modifier: Modifier = Modifier,
    // PullToRefreshBox parameters
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    indicator: (@Composable (BoxScope.(pullToRefreshState: PullToRefreshState) -> Unit))? = null,
    // LazyColumn parameters
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical = if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    overscrollEffect: OverscrollEffect? = rememberOverscrollEffect(),
    content: LazyListScope.() -> Unit,
) {
    val pullToRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        state = pullToRefreshState,
        isRefreshing = isRefreshing,
        onRefresh = { onRefresh() },
        indicator = {
            if (indicator != null) {
                indicator(pullToRefreshState)
            } else {
                TurninIndicator(isRefreshing, pullToRefreshState)
            }
        },
    ) {
        LazyColumn(
            modifier = modifier,
            state = state,
            contentPadding = contentPadding,
            reverseLayout = reverseLayout,
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
            flingBehavior = flingBehavior,
            userScrollEnabled = userScrollEnabled,
            overscrollEffect = overscrollEffect,
            content = content,
        )
    }
}
