package com.peekr.presentation.discover.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.component.icon.PeekrIcon
import com.peekr.core.designsystem.component.topbar.PeekrTopBar
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.icon.Arrow1Right
import com.peekr.core.designsystem.util.icon.PeekrIcons
import com.peekr.core.designsystem.util.token.ScreenTokens
import com.peekr.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.peekr.presentation.R
import com.peekr.presentation.discover.model.UiHistoryUser
import com.peekr.presentation.discover.state.DiscoverContract

/**
 * 탐색 화면 프레임
 *
 * @param modifier [Modifier]
 */
@Composable
private fun DiscoverScreenFrame(
    modifier: Modifier = Modifier,
    topBar: @Composable ColumnScope.() -> Unit,
    historyBar: @Composable ColumnScope.() -> Unit,
    currentTargetUser: @Composable ColumnScope.() -> Unit,
    users: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier.background(PeekrTheme.colorScheme.backgroundNormal)) {
        topBar()
        Spacer(Modifier.height(10.dp))
        historyBar()
        Spacer(Modifier.height(20.dp))
        currentTargetUser()
        Spacer(Modifier.height(20.dp))
        users()
    }
}

/**
 * 탐색 화면
 *
 * @param modifier [Modifier]
 */
@Composable
fun DiscoverScreen(
    modifier: Modifier = Modifier,
    uiState: DiscoverContract.UiState,
    onUiEvent: (DiscoverContract.UiEvent) -> Unit,
) {
    DiscoverScreenFrame(
        modifier = modifier,
        topBar = {
            PeekrTopBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ScreenTokens.HorizontalPadding),
                title = stringResource(R.string.discover_screen_top_bar_title),
            )
        },
        historyBar = {
            HistoryBar(
                modifier = Modifier.fillMaxWidth(),
                currentTargetUserId = uiState.currentTargetUserId,
                historyUsers = uiState.historyUsers,
                onItemClick = { historyUser ->
                    onUiEvent(
                        DiscoverContract.UiEvent.RefreshDiscoverContexts(historyUser.userId),
                    )
                },
            )
        },
        currentTargetUser = {},
        users = {},
    )
}

/**
 * 히스토리 바
 *
 * @param modifier [Modifier]
 * @param currentTargetUserId 현재 탐색 대상 사용자 ID
 * @param historyUsers 히스토리 아이템 리스트
 * @param onItemClick 히스토리 아이템 클릭 시 콜백
 */
@Composable
private fun HistoryBar(
    modifier: Modifier = Modifier,
    currentTargetUserId: Long?,
    historyUsers: List<UiHistoryUser>,
    onItemClick: (UiHistoryUser) -> Unit,
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = ScreenTokens.HorizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items(
            items = historyUsers,
            key = { it.userId },
        ) { historyUser ->
            UserChip(
                isSelected = historyUser.userId == currentTargetUserId,
                userChipInfo = historyUser,
                onClick = { onItemClick(historyUser) },
            )
            Spacer(Modifier.width(4.dp))
            PeekrIcon(
                modifier = Modifier.size(18.dp),
                icon = PeekrIcons.Default.Normal.Arrow1Right,
                contentDescription = null,
                tint = PeekrTheme.colorScheme.lineNormal,
            )
        }
    }
}

// ------------------------------ Previews ------------------------------
@PreviewLightDarkWithBackground
@Composable
private fun DiscoverScreenPreview() {
    PeekrAppTheme {
        DiscoverScreen(
            modifier = Modifier.fillMaxSize(),
            uiState = DiscoverContract.UiState(
                currentTargetUserId = 3L,
                historyUsers = UiHistoryUser.samples,
            ),
            onUiEvent = {},
        )
    }
}
