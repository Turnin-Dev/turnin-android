package com.peekr.presentation.home.view

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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.peekr.core.designsystem.component.avatar.PeekrAvatar
import com.peekr.core.designsystem.component.button.PeekrIconButton
import com.peekr.core.designsystem.component.icon.PeekrIconSize
import com.peekr.core.designsystem.component.skeleton.SkeletonBox
import com.peekr.core.designsystem.component.topbar.PeekrLogoTopBar
import com.peekr.core.designsystem.component.topbar.PeekrTopBarTokens
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.click.clickableSingle
import com.peekr.core.designsystem.util.icon.Bell
import com.peekr.core.designsystem.util.icon.PeekrIcons
import com.peekr.core.designsystem.util.token.ScreenTokens
import com.peekr.core.presentation.common.navigation.args.UserProfileArgs
import com.peekr.core.presentation.ui.component.error.FooterError
import com.peekr.core.presentation.ui.component.indicator.PeekrIndicator
import com.peekr.core.presentation.ui.component.lazycolumn.RefreshableLazyColumn
import com.peekr.core.presentation.ui.component.lazycolumn.pagingItem
import com.peekr.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.peekr.presentation.R
import com.peekr.presentation.home.model.UiFeed
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
    val topBarHeightPx = with(density) { PeekrTopBarTokens.Height.toPx() }
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
    val lazyListState = rememberLazyListState()
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
            PeekrLogoTopBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PeekrTheme.colorScheme.backgroundNormal)
                    .padding(
                        start = ScreenTokens.HorizontalPadding,
                        end = ScreenTokens.HorizontalPaddingWithTouchTarget,
                    ),
                optionSlot = {
                    PeekrIconButton(
                        icon = PeekrIcons.Outlined.Normal.Bell,
                        iconSize = PeekrIconSize.Small,
                        contentDescription = stringResource(R.string.home_screen_notification),
                        onClick = onNotificationClick,
                        tint = PeekrTheme.colorScheme.textNormal,
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
                    PeekrIndicator(
                        isRefreshing = isRefreshing,
                        state = state,
                        modifier = Modifier.offset(y = PeekrTopBarTokens.Height),
                    )
                },
                state = lazyListState,
                contentPadding = PaddingValues(
                    top = PeekrTopBarTokens.Height,
                    bottom = 20.dp, // 임시
                ),
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
                ) { idx ->
                    val feed = feeds[idx]
                    feed?.let {
                        Column {
                            if (idx == 0) {
                                FeedDivider()
                            }
                            Feed(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickableSingle { onFeedClick(it) },
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
                            FeedDivider()
                        }
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
    Column(modifier = modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
        // 사용자 정보 일부, 생성 일자
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 사용자 정보 일부
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PeekrAvatar(
                    modifier = Modifier.size(28.dp),
                    model = feed.profileImageUrl,
                    contentDescription = feed.userName,
                    onClick = onUserClick,
                )
                Text(
                    text = feed.userName,
                    style = PeekrTheme.typography.body4,
                    fontWeight = FontWeight.Bold,
                    color = PeekrTheme.colorScheme.textNormal,
                )
            }

            // 생성 일자
            Text(
                text = feed.createdAt,
                style = PeekrTheme.typography.body5,
                fontWeight = FontWeight.Normal,
                color = PeekrTheme.colorScheme.textAssist2,
            )
        }
        Spacer(Modifier.height(16.dp))
        // 키워드
        Text(
            text = feed.keyword,
            style = PeekrTheme.typography.headline2,
            fontWeight = FontWeight.Bold,
            color = PeekrTheme.colorScheme.textNormal,
        )
        Spacer(Modifier.height(4.dp))
        // 키워드 내용
        Text(
            text = feed.description,
            style = PeekrTheme.typography.body3Content,
            fontWeight = FontWeight.Normal,
            color = PeekrTheme.colorScheme.textNormal,
            maxLines = 10,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun FeedSkeleton() {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
        // 사용자 정보 일부, 생성 일자
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 사용자 정보 일부
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SkeletonBox(Modifier.size(28.dp), CircleShape)
                SkeletonBox(Modifier.size(63.dp, 10.dp))
            }
            // 생성 일자
            SkeletonBox(Modifier.size(48.dp, 10.dp))
        }
        Spacer(Modifier.height(16.dp))
        Column(modifier = Modifier.padding(top = 7.dp, bottom = 5.dp)) {
            SkeletonBox(Modifier.size(100.dp, 16.dp))
            Spacer(Modifier.height(12.dp))
            SkeletonBox(Modifier.size(262.dp, 12.dp))
            Spacer(Modifier.height(6.dp))
            SkeletonBox(Modifier.size(262.dp, 12.dp))
            Spacer(Modifier.height(6.dp))
            SkeletonBox(Modifier.size(246.dp, 12.dp))
        }
    }
}

/**
 * 피드 구분선
 */
@Composable
private fun FeedDivider() {
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        thickness = 0.5.dp,
        color = PeekrTheme.colorScheme.lineDivider,
    )
}

// ------------------------------ Previews ------------------------------
@PreviewLightDarkWithBackground
@Composable
private fun FeedPreview() {
    PeekrAppTheme {
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
    PeekrAppTheme {
        FeedSkeleton()
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    val feeds = testFeedsPagingData.collectAsLazyPagingItems()

    PeekrAppTheme {
        HomeScreen(
            modifier = Modifier
                .fillMaxSize()
                .background(PeekrTheme.colorScheme.backgroundNormal),
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
