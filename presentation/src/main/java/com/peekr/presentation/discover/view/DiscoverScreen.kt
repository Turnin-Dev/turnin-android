package com.peekr.presentation.discover.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.component.topbar.PeekrTopBar
import com.peekr.core.designsystem.util.token.ScreenTokens
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
    topBar: @Composable () -> Unit,
    historyBar: @Composable () -> Unit,
    currentTargetUser: @Composable () -> Unit,
    users: @Composable () -> Unit,
) {
    Column(modifier) {
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
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = ScreenTokens.HorizontalPadding),
                title = stringResource(R.string.discover_screen_top_bar_title),
            )
        },
        historyBar = {
            HistoryBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(ScreenTokens.HorizontalPadding),
                currentTargetUserId = uiState.currentTargetUserId,
                historyUsers = uiState.historyUsers,
                onItemClick = { historyUser ->
                    onUiEvent(DiscoverContract.UiEvent.OnSelectedHistoryUser(historyUser))
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
        horizontalArrangement = Arrangement.spacedBy(4.dp),
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
        }
    }
}
