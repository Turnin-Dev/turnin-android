package com.peekr.presentation.friend.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
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
import com.peekr.core.designsystem.component.tabBar.PeekrTabBar
import com.peekr.core.designsystem.component.topbar.PeekrTopBar
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.click.clickableSingle
import com.peekr.core.designsystem.util.token.ScreenTokens
import com.peekr.core.domain.friend.model.FriendStatus
import com.peekr.core.presentation.ui.component.button.FriendStatusButton
import com.peekr.core.presentation.ui.component.error.FooterError
import com.peekr.core.presentation.ui.component.lazycolumn.RefreshableLazyColumn
import com.peekr.core.presentation.ui.component.lazycolumn.pagingItem
import com.peekr.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.peekr.presentation.R
import com.peekr.presentation.friend.model.UiFriendInfo
import com.peekr.presentation.friend.model.UiRequester
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * 친구 목록 화면 프레임
 *
 * @param modifier [Modifier]
 * @param isMyFriendList 나의 친구 목록 여부
 * @param topBar 탑바
 * @param friends 친구 목록
 * @param requesters 받은 친구 요청 목록
 */
@Composable
private fun FriendListFrame(
    modifier: Modifier = Modifier,
    isMyFriendList: Boolean,
    topBar: @Composable () -> Unit,
    friends: @Composable PagerScope.() -> Unit,
    requesters: @Composable PagerScope.() -> Unit = {},
) {
    val tabs = if (isMyFriendList) {
        listOf(
            stringResource(R.string.friend_list_tab_bar_title_1),
            stringResource(R.string.friend_list_tab_bar_title_2),
        )
    } else {
        listOf(stringResource(R.string.friend_list_tab_bar_title_1))
    }

    Column(modifier) {
        PeekrTabBar(
            modifier = Modifier.fillMaxWidth(),
            tabs = tabs,
            pageContent = { page ->
                when {
                    page == 0 -> friends()
                    page == 1 && isMyFriendList -> requesters()
                    else -> {}
                }
            },
        )
        topBar()
    }
}

/**
 * 친구 목록 화면
 *
 * @param modifier [Modifier]
 * @param isMyFriendList 나의 친구 목록 여부
 * @param friends 친구 목록
 * @param requesters 친구 요청 목록
 * @param requestersStatus 친구 요청한 사용자의 상태 Map
 * @param onFriendClick 친구 클릭 시 콜백
 * @param onRequestAcceptClick 친구 요청 수락 시 콜백
 * @param onBackPressed 뒤로 가기 클릭 시 콜백
 */
@Composable
fun FriendListScreen(
    modifier: Modifier = Modifier,
    isMyFriendList: Boolean,
    friends: LazyPagingItems<UiFriendInfo>,
    requesters: LazyPagingItems<UiRequester>,
    requestersStatus: Map<Long, FriendStatus>,
    onFriendClick: (userId: Long) -> Unit,
    onRequestAcceptClick: (targetId: Long, status: FriendStatus) -> Unit,
    onBackPressed: () -> Unit,
) {
    FriendListFrame(
        modifier = modifier,
        isMyFriendList = isMyFriendList,
        topBar = {
            TopBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ScreenTokens.HorizontalPaddingWithTouchTarget),
                onBackPress = onBackPressed,
            )
        },
        friends = {
            FriendList(
                modifier = Modifier.fillMaxSize(),
                friends = friends,
                onFriendClick = { friend ->
                    onFriendClick(friend.id)
                },
            )
        },
        requesters = {
            RequesterList(
                modifier = Modifier.fillMaxWidth(),
                requesters = requesters,
                requestersStatus = requestersStatus,
                onRequesterClick = { requester ->
                    onFriendClick(requester.userId)
                },
                onRequestAcceptClick = { targetId, status ->
                    onRequestAcceptClick(targetId, status)
                },
            )
        },
    )
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
    var isRefreshing = remember {
        derivedStateOf {
            friends.loadState.refresh is LoadState.Loading
        }
    }.value

    // 친구 목록 + Footer
    RefreshableLazyColumn(
        modifier = modifier,
        isRefreshing = isRefreshing,
        onRefresh = { friends.refresh() },
        contentPadding = PaddingValues(bottom = 16.dp),
    ) {
        pagingItem(
            pagingItems = friends,
            key = friends.itemKey { it.id },
            skeletonCount = 20,
            skeleton = {
                FriendCardSkeleton()
            },
            initialError = {
                FooterError(
                    modifier = Modifier.fillMaxWidth(),
                    errorMessage = stringResource(R.string.friend_list_error_message_default),
                    onRetry = { friends.retry() },
                )
            },
            footerError = {
                FooterError(
                    modifier = Modifier.fillMaxWidth(),
                    errorMessage = stringResource(R.string.friend_list_error_message_default),
                    onRetry = { friends.retry() },
                )
            },
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
    }
}

/**
 * 친구 요청 목록
 *
 * @param modifier [Modifier]
 * @param requesters 친구 요청 목록
 * @param requestersStatus 친구 요청한 사용자의 상태 Map
 * @param onRequesterClick 요청자 클릭 시 콜백
 * @param onRequestAcceptClick 친구 요청 수락 시 콜백
 */
@Composable
private fun RequesterList(
    modifier: Modifier = Modifier,
    requesters: LazyPagingItems<UiRequester>,
    requestersStatus: Map<Long, FriendStatus>,
    onRequesterClick: (UiRequester) -> Unit,
    onRequestAcceptClick: (targetId: Long, status: FriendStatus) -> Unit,
) {
    var isRefreshing = remember {
        derivedStateOf {
            requesters.loadState.refresh is LoadState.Loading
        }
    }.value

    // 친구 요청 목록 + Footer
    RefreshableLazyColumn(
        modifier = modifier,
        isRefreshing = isRefreshing,
        onRefresh = { requesters.refresh() },
        contentPadding = PaddingValues(top = 10.dp, bottom = 16.dp),
    ) {
        pagingItem(
            pagingItems = requesters,
            key = requesters.itemKey { it.id },
            skeletonCount = 20,
            skeleton = {
                FriendCardSkeleton()
            },
            initialError = {
                FooterError(
                    modifier = Modifier.fillMaxWidth(),
                    errorMessage = stringResource(R.string.requester_list_error_message_default),
                    onRetry = { requesters.retry() },
                )
            },
            footerError = {
                FooterError(
                    modifier = Modifier.fillMaxWidth(),
                    errorMessage = stringResource(R.string.requester_list_error_message_default),
                    onRetry = { requesters.retry() },
                )
            },
        ) { idx ->
            val requester = requesters[idx]
            requester?.let {
                val requesterStatus = requestersStatus[requester.id] ?: FriendStatus.RECEIVED

                RequesterCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickableSingle(onClick = { onRequesterClick(it) })
                        .padding(horizontal = ScreenTokens.HorizontalPadding),
                    profileImageUrl = requester.profileImageUrl,
                    name = requester.name,
                    displayId = requester.displayId,
                    friendStatus = requesterStatus,
                    onFriendStatusClick = {
                        onRequestAcceptClick(requester.userId, requesterStatus)
                    },
                )
            }
        }
    }
}

/**
 * 요청자 항목
 *
 * @param modifier [Modifier]
 */
@Composable
private fun RequesterCard(
    modifier: Modifier = Modifier,
    profileImageUrl: String?,
    name: String,
    displayId: String,
    friendStatus: FriendStatus,
    onFriendStatusClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FriendCard(
            profileImageUrl = profileImageUrl,
            name = name,
            displayId = displayId,
        )

        FriendStatusButton(
            friendStatus = friendStatus,
            onClick = onFriendStatusClick,
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
                text = name,
                style = PeekrTheme.typography.body3Many,
                fontWeight = FontWeight.Bold,
                color = PeekrTheme.colorScheme.textNormal,
            )
            Text(
                text = displayId,
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
private fun FriendListScreenPreview() {
    val friends = testFriendsPagingData.collectAsLazyPagingItems()
    val requesters = testRequestersPagingData.collectAsLazyPagingItems()

    PeekrAppTheme {
        FriendListScreen(
            modifier = Modifier.fillMaxSize(),
            isMyFriendList = true,
            friends = friends,
            requesters = requesters,
            onFriendClick = {},
            requestersStatus = mapOf(),
            onRequestAcceptClick = { _, _ -> },
            onBackPressed = {},
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

private val testRequestersPagingData = MutableStateFlow(
    PagingData.from(
        List(40) {
            UiRequester(
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
