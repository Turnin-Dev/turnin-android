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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.peekr.core.designsystem.component.avatar.PeekrAvatar
import com.peekr.core.designsystem.component.button.PeekrIconButton
import com.peekr.core.designsystem.component.icon.PeekrIconSize
import com.peekr.core.designsystem.component.topbar.PeekrLogoTopBar
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.click.clickableSingle
import com.peekr.core.designsystem.util.icon.Bell
import com.peekr.core.designsystem.util.icon.PeekrIcons
import com.peekr.core.designsystem.util.token.ScreenTokens
import com.peekr.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.peekr.presentation.R
import com.peekr.presentation.home.model.UiFeed
import kotlin.math.roundToInt
import kotlin.text.get
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
private fun HomeScreenFrame(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit,
    contents: @Composable (topBarHeight: Dp) -> Unit,
) {
    val density = LocalDensity.current
    var topBarHeightPx by remember { mutableIntStateOf(0) }
    var topBarOffsetY by remember { mutableFloatStateOf(0f) }
    val nestedScroll = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = topBarOffsetY + delta
                topBarOffsetY = newOffset.coerceIn(-topBarHeightPx.toFloat(), 0f)
                return Offset.Zero
            }
        }
    }

    Box(
        modifier
            .fillMaxSize()
            .nestedScroll(nestedScroll),
    ) {
        contents(with(density) { topBarHeightPx.toDp() })

        Box(
            Modifier
                .onSizeChanged { topBarHeightPx = it.height }
                .offset { IntOffset(0, topBarOffsetY.roundToInt()) },
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
 * @param onNotificationClick 알림 클릭 시 콜백
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    feeds: LazyPagingItems<UiFeed>,
    onFeedClick: (UiFeed) -> Unit,
    onNotificationClick: () -> Unit,
) {
    HomeScreenFrame(
        modifier = modifier.fillMaxSize(),
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
                onLogoClick = {},
            )
        },
        contents = { topBarHeight ->
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(
                    top = topBarHeight,
                    bottom = 100.dp, // 임시
                ),
                overscrollEffect = null,
            ) {
                items(
                    count = feeds.itemCount,
                    key = feeds.itemKey { it.userKeywordId },
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
 */
@Composable
private fun Feed(
    modifier: Modifier = Modifier,
    feed: UiFeed,
) {
    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 20.dp)) {
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
        Spacer(Modifier.height(10.dp))
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
                    onClick = {},
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
        )
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
