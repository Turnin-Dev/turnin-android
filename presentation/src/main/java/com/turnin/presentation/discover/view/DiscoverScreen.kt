package com.turnin.presentation.discover.view

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.turnin.core.designsystem.component.fab.PeekrFab
import com.turnin.core.designsystem.component.icon.PeekrIcon
import com.turnin.core.designsystem.component.topbar.PeekrTopBar
import com.turnin.core.designsystem.theme.PeekrAppTheme
import com.turnin.core.designsystem.theme.PeekrTheme
import com.turnin.core.designsystem.util.icon.Arrow1Right
import com.turnin.core.designsystem.util.icon.PeekrIcons
import com.turnin.core.designsystem.util.icon.Refresh
import com.turnin.core.designsystem.util.token.ScreenTokens
import com.turnin.core.presentation.common.navigation.args.UserProfileArgs
import com.turnin.core.presentation.ui.component.EmptyGuidance
import com.turnin.core.presentation.ui.component.error.FooterError
import com.turnin.core.presentation.ui.component.lazycolumn.RefreshableLazyColumn
import com.turnin.core.presentation.ui.component.lazycolumn.pagingItem
import com.turnin.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.turnin.presentation.R
import com.turnin.presentation.discover.model.UiDiscoverContext
import com.turnin.presentation.discover.model.UiDiscoverKeyword
import com.turnin.presentation.discover.model.UiDiscoverUser
import com.turnin.presentation.discover.state.DiscoverContract
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
    discoverContexts: LazyPagingItems<UiDiscoverContext>,
    topBar: @Composable ColumnScope.() -> Unit,
    historyBar: @Composable BoxScope.() -> Unit,
    currentDiscoverTarget: @Composable () -> Unit,
    users: LazyListScope.() -> Unit,
) {
    val lazyListState = rememberLazyListState()
    var isManualRefresh by rememberSaveable { mutableStateOf(false) }
    val isRefreshing by remember {
        derivedStateOf {
            isManualRefresh && discoverContexts.loadState.refresh is LoadState.Loading
        }
    }

    LaunchedEffect(isRefreshing) {
        if (!isRefreshing) isManualRefresh = false
    }

    LaunchedEffect(discoverContexts.loadState.refresh) {
        if (discoverContexts.loadState.refresh is LoadState.Loading) {
            lazyListState.scrollToItem(0)
        }
    }

    Column(modifier.background(PeekrTheme.colorScheme.backgroundNormal)) {
        // 탑바
        topBar()

        // 히스토리 바
        Box(Modifier.padding(vertical = 10.dp)) {
            historyBar()
        }

        RefreshableLazyColumn(
            modifier = modifier,
            isRefreshing = isRefreshing,
            onRefresh = {
                isManualRefresh = true
                discoverContexts.refresh()
            },
            state = lazyListState,
            contentPadding = PaddingValues(
                // Fab 크기(약 50) + Fab 하단 패딩 + 여유 패딩 값(20)
                bottom = 50.dp + FabPaddingValues.calculateBottomPadding() + 20.dp,
                start = ScreenTokens.HorizontalPadding,
                end = ScreenTokens.HorizontalPadding,
            ),
            userScrollEnabled = discoverContexts.loadState.refresh !is LoadState.Loading,
        ) {
            // 현재 탐색 대상 타이틀
            item {
                Spacer(Modifier.height(20.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.discover_screen_current_target_user_title),
                    style = PeekrTheme.typography.body4,
                    fontWeight = FontWeight.Medium,
                    color = PeekrTheme.colorScheme.textAssist2,
                )
                Spacer(Modifier.height(16.dp))
            }

            // 현재 탐색 대상
            item {
                currentDiscoverTarget()
                Spacer(Modifier.height(20.dp))
            }

            // 사용자 리스트 타이틀
            item {
                Spacer(Modifier.height(20.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.discover_screen_users_title),
                    style = PeekrTheme.typography.body4,
                    fontWeight = FontWeight.Medium,
                    color = PeekrTheme.colorScheme.textAssist2,
                )
                Spacer(Modifier.height(16.dp))
            }

            // 사용자 리스트
            users()
        }
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
            discoverContexts = discoverContexts,
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
                        modifier = Modifier.fillMaxWidth(),
                        discoverContext = discoverContext,
                        onUserClick = {
                            val args = UserProfileArgs(
                                userId = discoverContext.user.userId,
                                userName = discoverContext.user.userName,
                                profileImageUrl = discoverContext.user.profileImageUrl,
                            )
                            onUiEvent(DiscoverContract.UiEvent.NavigateToUserProfile(args))
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
                users(
                    discoverContexts = discoverContexts,
                    selectedDiscoverContext = uiState.selectedDiscoverTarget,
                    onFeedClick = { discoverContext ->
                        onUiEvent(
                            DiscoverContract.UiEvent.SelectFeed(discoverContext),
                        )
                    },
                    onUserClick = { discoverContext ->
                        val args = UserProfileArgs(
                            userId = discoverContext.user.userId,
                            userName = discoverContext.user.userName,
                            profileImageUrl = discoverContext.user.profileImageUrl,
                        )
                        onUiEvent(DiscoverContract.UiEvent.NavigateToUserProfile(args))
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
        horizontalArrangement = Arrangement.spacedBy(HistoryItemGap),
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
            Spacer(Modifier.width(HistoryItemGap))
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
 * 사용자 리스트
 *
 * @param discoverContexts 탐색 컨텍스트 (페이징 데이터)
 * @param selectedDiscoverContext 선택된 탐색 컨텍스트
 * @param onFeedClick 탐색 피드 클릭 시 콜백
 * @param onUserClick 사용자 클릭 시 콜백
 * @param onKeywordClick 키워드 클릭 시 콜백
 */
private fun LazyListScope.users(
    discoverContexts: LazyPagingItems<UiDiscoverContext>,
    selectedDiscoverContext: UiDiscoverContext?,
    onFeedClick: (UiDiscoverContext) -> Unit,
    onUserClick: (UiDiscoverContext) -> Unit,
    onKeywordClick: (userId: Long, UiDiscoverKeyword) -> Unit,
) {
    pagingItem(
        pagingItems = discoverContexts,
        key = discoverContexts.itemKey { it.user.userId },
        skeletonCount = 5,
        skeleton = {
            DiscoverFeedSkeleton()
        },
        initialError = {
            FooterError(
                modifier = Modifier.fillMaxWidth(),
                errorMessage = stringResource(R.string.discover_error_paging_init_error),
                onRetry = { discoverContexts.refresh() },
            )
        },
        footerError = {
            FooterError(
                modifier = Modifier.fillMaxWidth(),
                errorMessage = stringResource(R.string.discover_error_paging_footer_error),
                onRetry = { discoverContexts.retry() },
            )
        },
        emptyGuidance = {
            EmptyGuidance(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.discover_screen_empty_guidance_title),
                description = buildString {
                    append(stringResource(R.string.discover_screen_empty_guidance_desc_1))
                    append("\n")
                    append("\n")
                    append(stringResource(R.string.discover_screen_empty_guidance_desc_2))
                },
            )
        },
    ) { idx ->
        val user = discoverContexts[idx]
        user?.let {
            if (idx > 0) Spacer(Modifier.height(16.dp))

            val onFeedClickLambda = remember(user.user.userId) { { onFeedClick(user) } }
            val onUserClickLambda = remember(user.user.userId) { { onUserClick(user) } }
            val onKeywordClickLambda = remember(user.user.userId) {
                { keyword: UiDiscoverKeyword -> onKeywordClick(user.user.userId, keyword) }
            }

            DiscoverFeed(
                modifier = Modifier.fillMaxWidth(),
                discoverContext = user,
                selected = selectedDiscoverContext?.user?.userId == user.user.userId,
                onFeedClick = onFeedClickLambda,
                onUserClick = onUserClickLambda,
                onKeywordClick = onKeywordClickLambda,
            )
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
        modifier = modifier,
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

private val HistoryItemGap = 8.dp
private val FabPaddingValues = PaddingValues(end = 20.dp, bottom = 24.dp)

// ------------------------------ Previews ------------------------------
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
