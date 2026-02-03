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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.peekr.core.designsystem.component.icon.PeekrIcon
import com.peekr.core.designsystem.component.topbar.PeekrTopBar
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.PeekrShadowType
import com.peekr.core.designsystem.util.icon.Arrow1Right
import com.peekr.core.designsystem.util.icon.PeekrIcons
import com.peekr.core.designsystem.util.peekrShadow
import com.peekr.core.designsystem.util.token.ScreenTokens
import com.peekr.core.presentation.ui.component.error.FooterError
import com.peekr.core.presentation.ui.component.lazycolumn.pagingItem
import com.peekr.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.peekr.presentation.R
import com.peekr.presentation.discover.model.UiDiscoverContext
import com.peekr.presentation.discover.model.UiDiscoverKeyword
import com.peekr.presentation.discover.model.UiDiscoverUser
import com.peekr.presentation.discover.state.DiscoverContract
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * 탐색 화면 프레임
 *
 * @param modifier [Modifier]
 * @param topBar 탑바
 * @param historyBar 히스토리 바
 * @param currentDiscoverTarget 현재 탐색 대상
 * @param users 사용자 리스트
 */
@Composable
private fun DiscoverScreenFrame(
    modifier: Modifier = Modifier,
    topBar: @Composable ColumnScope.() -> Unit,
    historyBar: @Composable ColumnScope.() -> Unit,
    currentDiscoverTarget: @Composable ColumnScope.() -> Unit,
    users: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier.background(PeekrTheme.colorScheme.backgroundNormal)) {
        // 탑바
        topBar()
        Spacer(Modifier.height(10.dp))

        // 히스토리 바
        historyBar()
        Spacer(Modifier.height(20.dp))

        // 현재 탐색 대상
        Text(
            modifier = Modifier.padding(horizontal = ScreenTokens.HorizontalPadding),
            text = stringResource(R.string.discover_screen_current_target_user_title),
            style = PeekrTheme.typography.label3,
            fontWeight = FontWeight.Medium,
            color = PeekrTheme.colorScheme.textAssist2,
        )
        Spacer(Modifier.height(4.dp))
        currentDiscoverTarget()
        Spacer(Modifier.height(20.dp))

        // 사용자 리스트
        Text(
            modifier = Modifier.padding(horizontal = ScreenTokens.HorizontalPadding),
            text = stringResource(R.string.discover_screen_users_title),
            style = PeekrTheme.typography.label3,
            fontWeight = FontWeight.Medium,
            color = PeekrTheme.colorScheme.textAssist2,
        )
        Spacer(Modifier.height(4.dp))
        FeedDivider(Modifier.fillMaxWidth())
        users()
        Spacer(Modifier.height(20.dp))
    }
}

/**
 * 탐색 화면
 *
 * @param modifier [Modifier]
 * @param uiState UI 상태
 * @param discoverContexts 탐색 컨텍스트 리스트
 * @param onUiEvent UI 이벤트
 */
@Composable
fun DiscoverScreen(
    modifier: Modifier = Modifier,
    uiState: DiscoverContract.UiState,
    discoverContexts: LazyPagingItems<UiDiscoverContext>,
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
                currentTargetUserId = uiState.currentDiscoverTarget?.user?.userId,
                histories = uiState.histories,
                onItemClick = { discoverContext ->
                    onUiEvent(
                        DiscoverContract.UiEvent.ChangeCurrentDiscoverTarget(discoverContext),
                    )
                },
            )
        },
        currentDiscoverTarget = {
            uiState.currentDiscoverTarget?.let {
                CurrentDiscoverTarget(
                    modifier = Modifier.padding(horizontal = ScreenTokens.HorizontalPadding),
                    discoverContext = it,
                    onFeedClick = {},
                    onUserClick = {},
                    onKeywordClick = {},
                )
            }
        },
        users = {
            Users(
                modifier = Modifier.fillMaxWidth(),
                users = discoverContexts,
                onFeedClick = {},
                onUserClick = {},
                onKeywordClick = {},
            )
        },
    )
}

/**
 * 히스토리 바
 *
 * @param modifier [Modifier]
 * @param currentTargetUserId 현재 탐색 대상 사용자 ID
 * @param histories 히스토리 아이템 리스트
 * @param onItemClick 히스토리 아이템 클릭 시 콜백
 */
@Composable
private fun HistoryBar(
    modifier: Modifier = Modifier,
    currentTargetUserId: Long?,
    histories: List<UiDiscoverContext>,
    onItemClick: (UiDiscoverContext) -> Unit,
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = ScreenTokens.HorizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items(
            items = histories,
            key = { it.user.userId },
        ) { history ->
            UserChip(
                isSelected = history.user.userId == currentTargetUserId,
                userChipInfo = history.user,
                onClick = { onItemClick(history) },
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

/**
 * 현재 탐색 대상
 *
 * @param modifier [Modifier]
 * @param discoverContext 탐색 컨텍스트 [UiDiscoverContext]
 * @param onFeedClick 피드 클릭 시 콜백
 * @param onUserClick 사용자 클릭 시 콜백
 * @param onKeywordClick 키워드 클릭 시 콜백
 */
@Composable
private fun CurrentDiscoverTarget(
    modifier: Modifier = Modifier,
    discoverContext: UiDiscoverContext,
    onFeedClick: () -> Unit,
    onUserClick: () -> Unit,
    onKeywordClick: (UiDiscoverKeyword) -> Unit,
) {
    DiscoverFeed(
        modifier = modifier
            .peekrShadow(type = PeekrShadowType.Normal, shape = CurrentTargetUserShape)
            .clip(CurrentTargetUserShape)
            .background(PeekrTheme.colorScheme.backgroundNormal, CurrentTargetUserShape),
        discoverContext = discoverContext,
        onFeedClick = {},
        onUserClick = {},
        onKeywordClick = {},
    )
}

@Composable
private fun Users(
    modifier: Modifier = Modifier,
    users: LazyPagingItems<UiDiscoverContext>,
    onFeedClick: (UiDiscoverContext) -> Unit,
    onUserClick: (UiDiscoverContext) -> Unit,
    onKeywordClick: (UiDiscoverKeyword) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
    ) {
        pagingItem(
            pagingItems = users,
            key = users.itemKey { it.user.userId },
            skeletonCount = 10,
            skeleton = {
                DiscoverFeedSkeleton()
            },
            initialError = {
                FooterError(
                    modifier = Modifier.fillMaxWidth(),
                    errorMessage = stringResource(R.string.discover_error_paging_init_error),
                    onRetry = { users.refresh() },
                )
            },
            footerError = {
                FooterError(
                    modifier = Modifier.fillMaxWidth(),
                    errorMessage = stringResource(R.string.discover_error_paging_footer_error),
                    onRetry = { users.retry() },
                )
            },
        ) { idx ->
            val user = users[idx]
            user?.let {
                Column {
                    DiscoverFeed(
                        modifier = Modifier.fillMaxWidth(),
                        discoverContext = user,
                        onFeedClick = { onFeedClick(user) },
                        onUserClick = { onUserClick(user) },
                        onKeywordClick = { keyword -> onKeywordClick(keyword) },
                    )
                    FeedDivider(Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun FeedDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier,
        thickness = 0.5.dp,
        color = PeekrTheme.colorScheme.lineDivider,
    )
}

private val CurrentTargetUserShape = RoundedCornerShape(8.dp)

// ------------------------------ Previews ------------------------------
@PreviewLightDarkWithBackground
@Composable
private fun CurrentTargetUserPreview() {
    PeekrAppTheme {
        CurrentDiscoverTarget(
            discoverContext = UiDiscoverContext.sample,
            onFeedClick = {},
            onUserClick = {},
            onKeywordClick = {},
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun DiscoverScreenPreview() {
    val discoverContexts = testDiscoverContexts.collectAsLazyPagingItems()

    PeekrAppTheme {
        DiscoverScreen(
            modifier = Modifier.fillMaxSize(),
            uiState = DiscoverContract.UiState(
                currentDiscoverTarget = UiDiscoverContext.sample,
                histories = listOf(UiDiscoverContext.sample),
            ),
            discoverContexts = discoverContexts,
            onUiEvent = {},
        )
    }
}

private val testDiscoverContexts = MutableStateFlow(
    PagingData.from(
        List(20) {
            val id = it + 1L
            UiDiscoverContext(
                user = UiDiscoverUser(
                    userId = id,
                    userName = "username$id",
                    displayId = "displayId$id",
                    profileImageUrl = null,
                ),
                keywords = List(5) {
                    val kid = it + 1L
                    UiDiscoverKeyword(
                        userKeywordId = kid,
                        keywordId = kid,
                        keywordName = "keyword$kid",
                    )
                },
            )
        },
    ),
)
