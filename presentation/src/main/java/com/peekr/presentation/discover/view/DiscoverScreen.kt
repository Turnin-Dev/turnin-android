package com.peekr.presentation.discover.view

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.peekr.core.designsystem.component.fab.PeekrFab
import com.peekr.core.designsystem.component.icon.PeekrIcon
import com.peekr.core.designsystem.component.topbar.PeekrTopBar
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.PeekrShadowType
import com.peekr.core.designsystem.util.icon.Arrow1Right
import com.peekr.core.designsystem.util.icon.PeekrIcons
import com.peekr.core.designsystem.util.icon.Refresh
import com.peekr.core.designsystem.util.peekrShadow
import com.peekr.core.designsystem.util.token.ScreenTokens
import com.peekr.core.presentation.ui.component.error.FooterError
import com.peekr.core.presentation.ui.component.lazycolumn.RefreshableLazyColumn
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
    Box(modifier) {
        DiscoverScreenFrame(
            modifier = Modifier.fillMaxSize(),
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
                uiState.currentDiscoverTarget?.let { discoverContext ->
                    CurrentDiscoverTarget(
                        modifier = Modifier.padding(horizontal = ScreenTokens.HorizontalPadding),
                        discoverContext = discoverContext,
                        onFeedClick = {
                            onUiEvent(
                                DiscoverContract.UiEvent.NavigateToUserProfile(
                                    userId = discoverContext.user.userId,
                                ),
                            )
                        },
                        onUserClick = {
                            onUiEvent(
                                DiscoverContract.UiEvent.NavigateToUserProfile(
                                    userId = discoverContext.user.userId,
                                ),
                            )
                        },
                        onKeywordClick = { keyword ->
                            onUiEvent(
                                DiscoverContract.UiEvent.NavigateToKeywordDetail(
                                    userId = discoverContext.user.userId,
                                    userKeywordId = keyword.userKeywordId,
                                ),
                            )
                        },
                    )
                }
            },
            users = {
                Users(
                    modifier = Modifier.fillMaxWidth(),
                    users = discoverContexts,
                    selectedDiscoverContext = uiState.selectedDiscoverTarget,
                    onFeedClick = { discoverContext ->
                        onUiEvent(
                            DiscoverContract.UiEvent.SelectFeed(discoverContext),
                        )
                    },
                    onUserClick = { discoverContext ->
                        onUiEvent(
                            DiscoverContract.UiEvent.NavigateToUserProfile(
                                userId = discoverContext.user.userId,
                            ),
                        )
                    },
                    onKeywordClick = { userId, keyword ->
                        onUiEvent(
                            DiscoverContract.UiEvent.NavigateToKeywordDetail(
                                userId = userId,
                                userKeywordId = keyword.userKeywordId,
                            ),
                        )
                    },
                )
            },
        )

        ReDiscoverFab(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(FabPaddingValues),
            enabled = uiState.selectedDiscoverTarget != null,
            onClick = {
                onUiEvent(DiscoverContract.UiEvent.ReDiscover)
            },
        )
    }
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
    val lazyListState = rememberLazyListState()
    val snapFormatter = rememberSnapFlingBehavior(lazyListState)
    LaunchedEffect(currentTargetUserId) {
        if (currentTargetUserId == null) return@LaunchedEffect
        val targetIndex = histories.indexOfFirst { it.user.userId == currentTargetUserId }
        if (targetIndex != -1) {
            lazyListState.animateScrollToItemCenter(targetIndex)
        }
    }

    LazyRow(
        modifier = modifier,
        state = lazyListState,
        contentPadding = PaddingValues(horizontal = ScreenTokens.HorizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        flingBehavior = snapFormatter,
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

suspend fun LazyListState.animateScrollToItemCenter(index: Int) {
    val layoutInfo = this.layoutInfo
    // 실제 보이는 영역의 너비 계산
    val viewportWidth = layoutInfo.viewportEndOffset + layoutInfo.viewportStartOffset

    // 첫 번째 아이템의 사이즈를 추정치로 사용하거나, 현재 보이는 아이템에서 찾음
    val itemSize = layoutInfo.visibleItemsInfo.firstOrNull()?.size ?: 0
    val centerOffset = (viewportWidth / 2) - (itemSize / 2)

    // 마이너스 오프셋을 주어 아이템을 중앙으로 당김
    this.animateScrollToItem(index, -centerOffset)
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
        selected = false,
        onFeedClick = onFeedClick,
        onUserClick = onUserClick,
        onKeywordClick = onKeywordClick,
    )
}

@Composable
private fun Users(
    modifier: Modifier = Modifier,
    users: LazyPagingItems<UiDiscoverContext>,
    selectedDiscoverContext: UiDiscoverContext?,
    onFeedClick: (UiDiscoverContext) -> Unit,
    onUserClick: (UiDiscoverContext) -> Unit,
    onKeywordClick: (userId: Long, UiDiscoverKeyword) -> Unit,
) {
    val lazyListState = rememberLazyListState()
    var isManualRefresh by rememberSaveable { mutableStateOf(false) }
    val isRefreshing by remember {
        derivedStateOf {
            isManualRefresh && users.loadState.refresh is LoadState.Loading
        }
    }

    LaunchedEffect(isRefreshing) {
        if (!isRefreshing) isManualRefresh = false
    }

    LaunchedEffect(users.loadState.refresh) {
        if (users.loadState.refresh is LoadState.Loading) {
            lazyListState.scrollToItem(0)
        }
    }

    RefreshableLazyColumn(
        modifier = modifier,
        isRefreshing = isRefreshing,
        onRefresh = {
            isManualRefresh = true
            users.refresh()
        },
        state = lazyListState,
        contentPadding = PaddingValues(
            // 마지막에 더해주는 20.dp는 여유 패딩 값
            bottom = FabSize + FabPaddingValues.calculateBottomPadding() + 20.dp,
        ),
        userScrollEnabled = users.loadState.refresh !is LoadState.Loading,
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
                        selected = selectedDiscoverContext?.user?.userId == user.user.userId,
                        onFeedClick = { onFeedClick(user) },
                        onUserClick = { onUserClick(user) },
                        onKeywordClick = { keyword -> onKeywordClick(user.user.userId, keyword) },
                    )
                    FeedDivider(Modifier.fillMaxWidth())
                }
            }
        }
    }
}

/**
 * 재탐색 Fab
 *
 * @param modifier [Modifier]
 * @param enabled 활성화 여부
 * @param onClick 재탐색 수행 콜백
 */
@Composable
private fun ReDiscoverFab(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    PeekrFab(
        modifier = modifier.size(FabSize),
        icon = PeekrIcons.Default.Normal.Refresh,
        contentDescription = stringResource(R.string.discover_screen_fab_content_desc),
        enabled = enabled,
        text = stringResource(R.string.discover_screen_fab_text),
        shape = CircleShape,
        onClick = {
            if (enabled) {
                onClick()
            }
        },
    )
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
private val FabSize = 50.dp
private val FabPaddingValues = PaddingValues(end = 20.dp, bottom = 24.dp)

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
private fun HistoryBarPreview() {
    var target: Long? by remember { mutableStateOf(null) }

    PeekrAppTheme {
        HistoryBar(
            modifier = Modifier.fillMaxWidth(),
            currentTargetUserId = target,
            histories = testDiscoverContexts,
            onItemClick = {
                target = it.user.userId
            },
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun DiscoverScreenPreview() {
    val discoverContexts = testDiscoverContextsPaging.collectAsLazyPagingItems()

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

private val testDiscoverContexts = List(20) {
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
}

private val testDiscoverContextsPaging =
    MutableStateFlow(PagingData.from(testDiscoverContexts))
