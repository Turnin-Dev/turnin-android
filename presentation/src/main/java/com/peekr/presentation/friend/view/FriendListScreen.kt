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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.peekr.core.designsystem.component.skeleton.SkeletonBox
import com.peekr.core.designsystem.component.tabBar.PeekrTabBar
import com.peekr.core.designsystem.component.topbar.PeekrTopBar
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.util.click.clickableSingle
import com.peekr.core.designsystem.util.token.ScreenTokens
import com.peekr.core.domain.friend.model.FriendStatus
import com.peekr.core.presentation.ui.component.EmptyGuidance
import com.peekr.core.presentation.ui.component.button.FriendStatusButton
import com.peekr.core.presentation.ui.component.card.ProfileCard
import com.peekr.core.presentation.ui.component.card.ProfileCardTokens
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
        topBar()
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
 * @param loadRequestersPagingData 친구 요청 목록 화면 진입 시 로드 이벤트
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
    loadRequestersPagingData: () -> Unit,
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
                isMyFriendList = isMyFriendList,
                friends = friends,
                onFriendClick = { friend ->
                    onFriendClick(friend.userId)
                },
            )
        },
        requesters = {
            LaunchedEffect(Unit) {
                loadRequestersPagingData()
            }

            RequesterList(
                modifier = Modifier.fillMaxSize(),
                isMyFriendList = isMyFriendList,
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
 * @param isMyFriendList 나의 친구 목록 여부
 * @param friends 친구 목록
 * @param onFriendClick 친구 클릭 시 콜백
 */
@Composable
private fun FriendList(
    modifier: Modifier = Modifier,
    isMyFriendList: Boolean,
    friends: LazyPagingItems<UiFriendInfo>,
    onFriendClick: (UiFriendInfo) -> Unit,
) {
    val isRefreshing = remember {
        derivedStateOf {
            friends.loadState.refresh is LoadState.Loading
        }
    }.value

    // 친구 목록 + Footer
    RefreshableLazyColumn(
        modifier = modifier,
        isRefreshing = isRefreshing,
        onRefresh = { friends.refresh() },
        contentPadding = ListContentPadding,
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
            emptyGuidance = {
                FriendsEmptyGuidance(isMyFriendList)
            },
        ) { idx ->
            val friend = friends[idx]
            friend?.let {
                ProfileCard(
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
 * @param isMyFriendList 나의 친구 목록 여부
 * @param requesters 친구 요청 목록
 * @param requestersStatus 친구 요청한 사용자의 상태 Map
 * @param onRequesterClick 요청자 클릭 시 콜백
 * @param onRequestAcceptClick 친구 요청 수락 시 콜백
 */
@Composable
private fun RequesterList(
    modifier: Modifier = Modifier,
    isMyFriendList: Boolean,
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
        contentPadding = ListContentPadding,
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
            emptyGuidance = {
                RequestersEmptyGuidance()
            },
        ) { idx ->
            val requester = requesters[idx]
            requester?.let {
                val requesterStatus = requestersStatus[requester.userId]
                    ?: FriendStatus.RECEIVED

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
        ProfileCard(
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
            modifier = Modifier.size(ProfileCardTokens.AvatarSize),
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

/**
 * 친구 목록 빈 화면 안내 뷰
 *
 * @param isMyFriendList 나의 친구 목록 여부
 */
@Composable
private fun FriendsEmptyGuidance(isMyFriendList: Boolean) {
    if (isMyFriendList) {
        EmptyGuidance(
            title = stringResource(R.string.friend_list_empty_guidance_title),
            description = stringResource(R.string.friend_list_empty_guidance_desc),
        )
    } else {
        EmptyGuidance(title = stringResource(R.string.friend_list_empty_guidance_title))
    }
}

/**
 * 받은 요청 목록 빈 화면 안내 뷰
 */
@Composable
private fun RequestersEmptyGuidance() {
    EmptyGuidance(
        title = stringResource(R.string.requester_list_empty_guidance_title),
    )
}

private val ListContentPadding = PaddingValues(top = 10.dp, bottom = 16.dp)

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
            isMyFriendList = false,
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
            loadRequestersPagingData = {},
            onFriendClick = {},
            requestersStatus = mapOf(),
            onRequestAcceptClick = { _, _ -> },
            onBackPressed = {},
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun FriendListScreen_Empty_Preview() {
    val friends = testFriendsEmptyPagingData.collectAsLazyPagingItems()
    val requesters = testRequestersEmptyPagingData.collectAsLazyPagingItems()

    PeekrAppTheme {
        FriendListScreen(
            modifier = Modifier.fillMaxSize(),
            isMyFriendList = true,
            friends = friends,
            requesters = requesters,
            loadRequestersPagingData = {},
            onFriendClick = {},
            requestersStatus = mapOf(),
            onRequestAcceptClick = { _, _ -> },
            onBackPressed = {},
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun FriendListScreen_Empty_Preview2() {
    val friends = testFriendsEmptyPagingData.collectAsLazyPagingItems()
    val requesters = testRequestersEmptyPagingData.collectAsLazyPagingItems()

    PeekrAppTheme {
        FriendListScreen(
            modifier = Modifier.fillMaxSize(),
            isMyFriendList = false,
            friends = friends,
            requesters = requesters,
            loadRequestersPagingData = {},
            onFriendClick = {},
            requestersStatus = mapOf(),
            onRequestAcceptClick = { _, _ -> },
            onBackPressed = {},
        )
    }
}

private val testFriendsEmptyPagingData = MutableStateFlow(
    PagingData.from(emptyList<UiFriendInfo>()),
)

private val testRequestersEmptyPagingData = MutableStateFlow(
    PagingData.from(emptyList<UiRequester>()),
)

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
