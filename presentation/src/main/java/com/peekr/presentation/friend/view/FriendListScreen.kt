package com.peekr.presentation.friend.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.peekr.core.designsystem.component.avatar.PeekrAvatar
import com.peekr.core.designsystem.component.skeleton.SkeletonBox
import com.peekr.core.designsystem.component.topbar.PeekrTopBar
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.click.clickableSingle
import com.peekr.core.designsystem.util.token.ScreenTokens
import com.peekr.core.presentation.ui.component.indicator.PeekrIndicator
import com.peekr.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.peekr.presentation.R
import com.peekr.presentation.friend.model.UiFriendInfo
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * 친구 목록 화면
 *
 * @param modifier [Modifier]
 * @param friends 친구 목록
 * @param onFriendClick 친구 클릭 시 콜백
 * @param onBackPress 뒤로 가기 클릭 시 콜백
 */
@Composable
fun FriendListScreen(
    modifier: Modifier = Modifier,
    friends: LazyPagingItems<UiFriendInfo>,
    onFriendClick: (UiFriendInfo) -> Unit,
    onBackPress: () -> Unit,
) {
    FriendListFrame(
        modifier = modifier,
        topBar = {
            TopBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ScreenTokens.HorizontalPaddingWithTouchTarget),
                onBackPress = onBackPress,
            )
        },
        contents = {
            FriendList(
                modifier = Modifier.fillMaxSize(),
                friends = friends,
                onFriendClick = onFriendClick,
            )
        },
    )
}

/**
 * 친구 목록 화면 프레임
 *
 * @param modifier [Modifier]
 * @param topBar 탑바
 * @param contents 메인 컨텐츠 (친구 목록)
 */
@Composable
private fun FriendListFrame(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit,
    contents: @Composable () -> Unit,
) {
    Column(modifier) {
        topBar()
        contents()
    }
}

/**
 * 탑바
 *
 * @param modifier [Modifier]
 * @param onBackPress 뒤로가기 콜백
 */
@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    onBackPress: () -> Unit,
) {
    PeekrTopBar(
        modifier = modifier,
        title = stringResource(R.string.friend_list_top_bar_title),
        onBackPressed = onBackPress,
    )
}

/**
 * 친구 목록
 *
 * @param modifier [Modifier]
 * @param friends 친구 목록
 * @param onFriendClick 친구 클릭 시 콜백
 */
@Composable
private fun FriendList(
    modifier: Modifier = Modifier,
    friends: LazyPagingItems<UiFriendInfo>,
    onFriendClick: (UiFriendInfo) -> Unit,
) {
    val pullToRefreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }
    LaunchedEffect(friends.loadState.refresh) {
        isRefreshing = when (friends.loadState.refresh) {
            LoadState.Loading -> true
            else -> false
        }
    }

    // 당겨서 새로고침 영역
    PullToRefreshBox(
        modifier = modifier,
        state = pullToRefreshState,
        isRefreshing = isRefreshing,
        onRefresh = { friends.refresh() },
        indicator = { PeekrIndicator(isRefreshing, pullToRefreshState) },
    ) {
        // 친구 목록 + Footer
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            // 초기 에러 발생 시
            val refreshState = friends.loadState.refresh
            if (refreshState is LoadState.Error && friends.itemCount == 0) {
                item {
                    FooterError(
                        modifier = Modifier.fillMaxWidth(),
                        errorMessage = stringResource(R.string.friend_list_error_message_default),
                        onRetry = { friends.retry() },
                    )
                }
            }

            // 초기 로딩 시 스켈레톤 표시
            if (isRefreshing && friends.itemCount == 0) {
                items(20) {
                    FriendCardSkeleton()
                }
            } else {
                // 친구 목록 표시
                items(
                    count = friends.itemCount,
                    key = friends.itemKey { it.id },
                ) { idx ->
                    val friend = friends[idx]
                    friend?.let {
                        FriendCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickableSingle(onClick = { onFriendClick(it) })
                                .padding(horizontal = ScreenTokens.HorizontalPadding),
                            profileImageUrl = it.profileImageUrl,
                            name = it.name,
                            displayId = it.displayId,
                        )
                    }
                }

                // 상태 별 Footer UI
                when (val appendState = friends.loadState.append) {
                    is LoadState.Loading -> {
                        // 로딩 시
                        item {
                            FriendCardSkeleton()
                        }
                    }

                    is LoadState.Error -> {
                        // 에러 발생 시
                        item {
                            FooterError(
                                modifier = Modifier.fillMaxWidth(),
                                errorMessage = stringResource(R.string.friend_list_error_message_default),
                                onRetry = { friends.retry() },
                            )
                        }
                    }

                    is LoadState.NotLoading -> {
                        // 더 이상 로드할 데이터가 없거나 정상 상태 or 마지막 페이지인 상태
                        // 아무 것도 보여주지 않는다.
                    }
                }
            }
        }
    }
}

/**
 * 에러 발생 시 목록 하단에 표시할 footer
 *
 * @param modifier [Modifier]
 * @param errorMessage 에러 메시지
 * @param onRetry 재시도 로직
 */
@Composable
private fun FooterError(
    modifier: Modifier = Modifier,
    errorMessage: String,
    onRetry: () -> Unit,
) {
    Column(
        modifier = modifier.heightIn(min = 78.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp, alignment = Alignment.CenterVertically),
    ) {
        Text(
            text = errorMessage,
            style = PeekrTheme.typography.label1,
            fontWeight = FontWeight.Medium,
            color = PeekrTheme.colorScheme.textAssist2,
        )
        Text(
            modifier = Modifier.clickableSingle(onClick = onRetry),
            text = stringResource(R.string.friend_list_error_retry),
            style = PeekrTheme.typography.label1,
            fontWeight = FontWeight.Bold,
            color = PeekrTheme.colorScheme.textStrong,
        )
    }
}

/**
 * 친구 항목
 *
 * @param modifier [Modifier]
 * @param profileImageUrl 친구 프로필 사진 url
 * @param name 친구 이름
 * @param displayId 친구 사용자 표시 ID
 */
@Composable
private fun FriendCard(
    modifier: Modifier = Modifier,
    profileImageUrl: String?,
    name: String,
    displayId: String,
) {
    Row(
        modifier = modifier.padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PeekrAvatar(
            modifier = Modifier.size(AvatarSize),
            model = profileImageUrl,
            contentDescription = name,
        )
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(2.dp, alignment = Alignment.CenterVertically),
        ) {
            Text(
                text = displayId,
                style = PeekrTheme.typography.body3Many,
                fontWeight = FontWeight.Bold,
                color = PeekrTheme.colorScheme.textNormal,
            )
            Text(
                text = name,
                style = PeekrTheme.typography.body4,
                fontWeight = FontWeight.Normal,
                color = PeekrTheme.colorScheme.textAssist,
            )
        }
    }
}

/**
 * 친구 항목 스켈레톤
 */
@Composable
private fun FriendCardSkeleton(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = ScreenTokens.HorizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SkeletonBox(
            modifier = Modifier.size(AvatarSize),
            shape = CircleShape,
        )
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterVertically),
        ) {
            SkeletonBox(Modifier.size(87.dp, 14.dp))
            SkeletonBox(Modifier.size(54.dp, 12.dp))
        }
    }
}

private val AvatarSize = 58.dp

// ------------------------------ Previews ------------------------------
@PreviewLightDarkWithBackground
@Composable
private fun TopBarPreview() {
    PeekrAppTheme {
        TopBar(
            modifier = Modifier.fillMaxWidth(),
            onBackPress = {},
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun FriendCardPreview() {
    PeekrAppTheme {
        FriendCard(
            modifier = Modifier.fillMaxWidth(),
            profileImageUrl = null,
            displayId = "Display ID",
            name = "name",
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun FriendCardSkeletonPreview() {
    PeekrAppTheme {
        FriendCardSkeleton(Modifier.fillMaxWidth())
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun FriendListPreview() {
    val friends = testFriendsPagingData.collectAsLazyPagingItems()

    PeekrAppTheme {
        FriendList(
            modifier = Modifier.fillMaxSize(),
            friends = friends,
            onFriendClick = {},
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun FooterErrorPreview() {
    PeekrAppTheme {
        FooterError(
            modifier = Modifier.fillMaxWidth(),
            errorMessage = "에러가 발생했어요.",
            onRetry = {},
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun FriendListScreenPreview() {
    val friends = testFriendsPagingData.collectAsLazyPagingItems()

    PeekrAppTheme {
        FriendListScreen(
            modifier = Modifier.fillMaxSize(),
            friends = friends,
            onFriendClick = {},
            onBackPress = {},
        )
    }
}

private val testFriendsPagingData = MutableStateFlow(
    PagingData.from(
        List(40) {
            UiFriendInfo(
                id = it.toLong(),
                userId = it.toLong(),
                displayId = "DisplayID $it",
                name = "Name $it",
                profileImageUrl = null,
                respondedAt = 1000,
                createdAt = 1000,
                updatedAt = 1000,
            )
        },
    ),
)
