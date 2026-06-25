package com.turnin.presentation.home.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.turnin.core.designsystem.component.avatar.TurninAvatar
import com.turnin.core.designsystem.component.button.TurninIconButton
import com.turnin.core.designsystem.component.icon.TurninIconSize
import com.turnin.core.designsystem.component.skeleton.SkeletonBox
import com.turnin.core.designsystem.component.topbar.TurninLogoTopBar
import com.turnin.core.designsystem.component.topbar.TurninTopBarTokens
import com.turnin.core.designsystem.theme.TurninAppTheme
import com.turnin.core.designsystem.theme.TurninTheme
import com.turnin.core.designsystem.util.click.clickableSingleWithoutRipple
import com.turnin.core.designsystem.util.icon.Bell
import com.turnin.core.designsystem.util.icon.TurninIcons
import com.turnin.core.designsystem.util.token.ScreenTokens
import com.turnin.core.domain.common.error.PagingApiCallException
import com.turnin.core.presentation.common.error.asUiText
import com.turnin.core.presentation.common.navigation.args.UserProfileArgs
import com.turnin.core.presentation.ui.component.error.FooterError
import com.turnin.core.presentation.ui.component.indicator.TurninIndicator
import com.turnin.core.presentation.ui.component.lazycolumn.RefreshableLazyColumn
import com.turnin.core.presentation.ui.component.lazycolumn.pagingItem
import com.turnin.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.turnin.presentation.R
import com.turnin.presentation.home.model.UiFeed
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * 홈 화면 프레임
 *
 * @param modifier [Modifier]
 * @param canTopBarControl 탑바 제어 가능 여부
 * @param topBar 탑바
 * @param contents 컨텐츠
 */
@Composable
private fun HomeScreenFrame(
    modifier: Modifier = Modifier,
    canTopBarControl: Boolean,
    topBar: @Composable () -> Unit,
    contents: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val topBarHeightPx = with(density) { TurninTopBarTokens.Height.toPx() }
    var topBarOffsetY by rememberSaveable { mutableFloatStateOf(0f) }
    val canTopBarControlState by rememberUpdatedState(canTopBarControl)
    val nestedScroll = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (canTopBarControlState) {
                    val delta = available.y
                    val newOffset = topBarOffsetY + delta
                    topBarOffsetY = newOffset.coerceIn(-topBarHeightPx, 0f)
                }
                return Offset.Zero
            }
        }
    }

    LaunchedEffect(canTopBarControl) {
        if (!canTopBarControl) {
            topBarOffsetY = 0f
        }
    }

    Box(
        modifier
            .fillMaxSize()
            .nestedScroll(nestedScroll),
    ) {
        contents()

        Box(
            Modifier
                .offset {
                    IntOffset(0, if (canTopBarControlState) topBarOffsetY.roundToInt() else 0)
                },
        ) {
            topBar()
        }
    }
}

/**
 * 홈 화면
 *
 * @param modifier [Modifier]
 * @param feeds 피드 리스트
 * @param onFeedClick 피드 클릭 시 콜백
 * @param onUserClick 사용자 클릭 시 콜백
 * @param onNotificationClick 알림 클릭 시 콜백
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    feeds: LazyPagingItems<UiFeed>,
    onFeedClick: (UiFeed) -> Unit,
    onUserClick: (UserProfileArgs) -> Unit,
    onNotificationClick: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = feeds.rememberLazyListState()
    var isManualRefresh by rememberSaveable { mutableStateOf(false) }
    val isRefreshing by remember {
        derivedStateOf {
            isManualRefresh && feeds.loadState.refresh is LoadState.Loading
        }
    }
    var isRefreshTriggered by remember { mutableStateOf(false) }
    val isFirstItemScrolled by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0 ||
                lazyListState.firstVisibleItemScrollOffset > 0
        }
    }

    LaunchedEffect(isRefreshing) {
        if (!isRefreshing) isManualRefresh = false
    }

    // 새로고침 시 맨 위로 스크롤
    LaunchedEffect(isRefreshing) {
        if (!isRefreshing && isRefreshTriggered) {
            lazyListState.requestScrollToItem(0)
            isRefreshTriggered = false
        }
    }

    HomeScreenFrame(
        modifier = modifier.fillMaxSize(),
        canTopBarControl = isFirstItemScrolled,
        topBar = {
            TurninLogoTopBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TurninTheme.colorScheme.backgroundNormal)
                    .padding(
                        start = ScreenTokens.HorizontalPadding,
                        end = ScreenTokens.HorizontalPaddingWithTouchTarget,
                    ),
                optionSlot = {
                    TurninIconButton(
                        icon = TurninIcons.Outlined.Normal.Bell,
                        iconSize = TurninIconSize.Normal,
                        contentDescription = stringResource(R.string.home_screen_notification),
                        onClick = onNotificationClick,
                        tint = TurninTheme.colorScheme.textNormal,
                    )
                },
                onLogoClick = {
                    coroutineScope.launch {
                        lazyListState.animateScrollToItem(0)
                    }
                },
            )
        },
        contents = {
            RefreshableLazyColumn(
                modifier = Modifier.fillMaxWidth(),
                isRefreshing = isRefreshing,
                onRefresh = {
                    isManualRefresh = true
                    isRefreshTriggered = true
                    feeds.refresh()
                },
                indicator = { state ->
                    TurninIndicator(
                        isRefreshing = isRefreshing,
                        state = state,
                        modifier = Modifier.offset(y = TurninTopBarTokens.Height),
                    )
                },
                state = lazyListState,
                contentPadding = PaddingValues(
                    top = TurninTopBarTokens.Height,
                    bottom = 40.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                overscrollEffect = null,
            ) {
                pagingItem(
                    pagingItems = feeds,
                    key = feeds.itemKey { it.userKeywordId },
                    skeletonCount = 10,
                    skeleton = {
                        FeedSkeleton()
                    },
                    initialError = {
                        FooterError(
                            modifier = Modifier.fillMaxWidth(),
                            errorMessage = stringResource(R.string.home_screen_error_message_default),
                            onRetry = { feeds.retry() },
                        )
                    },
                    footerError = {
                        FooterError(
                            modifier = Modifier.fillMaxWidth(),
                            errorMessage = stringResource(R.string.home_screen_error_message_default),
                            onRetry = { feeds.retry() },
                        )
                    },
                    refreshError = { e ->
                        val errorMessage = if (e is PagingApiCallException) {
                            e.error.asUiText().asString()
                        } else {
                            stringResource(R.string.home_screen_error_message_default)
                        }
                        FooterError(
                            modifier = Modifier.fillMaxWidth(),
                            errorMessage = errorMessage,
                            onRetry = { feeds.retry() },
                        )
                    },
                ) { idx ->
                    val feed = feeds[idx]
                    feed?.let {
                        Feed(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickableSingleWithoutRipple { onFeedClick(it) },
                            feed = it,
                            onUserClick = {
                                val args = UserProfileArgs(
                                    userId = it.userId,
                                    userName = it.userName,
                                    profileImageUrl = it.profileImageUrl,
                                )
                                onUserClick(args)
                            },
                        )
                    }
                }
            }
        },
    )
}

/**
 * 피드 뷰
 *
 * @param modifier [Modifier]
 * @param feed 피드
 * @param onUserClick 사용자 클릭 시 콜백
 */
@Composable
private fun Feed(
    modifier: Modifier = Modifier,
    feed: UiFeed,
    onUserClick: () -> Unit,
) {
    Column(modifier = modifier.padding(horizontal = 30.dp, vertical = 20.dp)) {
        // 사용자 정보 일부, 생성 일자
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 사용자 정보 일부
            Row(
                modifier = Modifier.clickableSingleWithoutRipple(onClick = onUserClick),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TurninAvatar(
                    modifier = Modifier.size(FeedAvatarSize),
                    model = feed.profileImageUrl,
                    contentDescription = feed.userName,
                    onClick = onUserClick,
                )
                Text(
                    text = feed.userName,
                    style = TurninTheme.typography.body4,
                    fontWeight = FontWeight.Bold,
                    color = TurninTheme.colorScheme.textNormal,
                )
            }

            // 생성 일자
            Text(
                text = feed.createdAt,
                style = TurninTheme.typography.body5,
                fontWeight = FontWeight.Normal,
                color = TurninTheme.colorScheme.textAssist2,
            )
        }

        Spacer(Modifier.height(16.dp))

        // 키워드
        Text(
            text = feed.keyword,
            style = TurninTheme.typography.headline2,
            fontWeight = FontWeight.Bold,
            color = TurninTheme.colorScheme.textNormal,
        )

        Spacer(Modifier.height(8.dp))

        // 키워드 내용
        Text(
            text = feed.description,
            style = TurninTheme.typography.bodyContent,
            fontWeight = FontWeight.Normal,
            color = TurninTheme.colorScheme.textNormal,
            maxLines = 10,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun FeedSkeleton() {
    Column(modifier = Modifier.padding(horizontal = 30.dp, vertical = 20.dp)) {
        // 사용자 정보 일부, 생성 일자
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 사용자 정보 일부
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SkeletonBox(Modifier.size(FeedAvatarSize), CircleShape)
                SkeletonBox(Modifier.size(63.dp, 12.dp))
            }

            // 생성 일자
            SkeletonBox(Modifier.size(49.dp, 12.dp))
        }

        Spacer(Modifier.height(16.dp))

        // 키워드
        SkeletonBox(Modifier.size(100.dp, 28.dp))

        Spacer(Modifier.height(8.dp))

        // 키워드 내용
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            SkeletonBox(Modifier.size(262.dp, 16.dp))
            SkeletonBox(Modifier.size(262.dp, 16.dp))
            SkeletonBox(Modifier.size(247.dp, 16.dp))
        }
    }
}

private val FeedAvatarSize = 28.dp

// ------------------------------ Previews ------------------------------
@PreviewLightDarkWithBackground
@Composable
private fun FeedPreview() {
    TurninAppTheme {
        Feed(
            modifier = Modifier.fillMaxWidth(),
            feed = UiFeed.sample,
            onUserClick = {},
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun FeedSkeletonPreview() {
    TurninAppTheme {
        FeedSkeleton()
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun HomeScreenPreview() {
    val feeds = testFeedsPagingData.collectAsLazyPagingItems()

    TurninAppTheme {
        HomeScreen(
            modifier = Modifier
                .fillMaxSize()
                .background(TurninTheme.colorScheme.backgroundNormal),
            feeds = feeds,
            onFeedClick = {},
            onUserClick = {},
            onNotificationClick = {},
        )
    }
}

private val testFeedsPagingData = MutableStateFlow(
    PagingData.from(
        List(40) {
            UiFeed.sample.copy(userKeywordId = it + 1L)
        },
    ),
)

@Composable
private fun <T : Any> LazyPagingItems<T>.rememberLazyListState(): LazyListState = when (itemCount) {
    0 -> remember(this) { LazyListState(0, 0) }
    else -> androidx.compose.foundation.lazy.rememberLazyListState()
}
