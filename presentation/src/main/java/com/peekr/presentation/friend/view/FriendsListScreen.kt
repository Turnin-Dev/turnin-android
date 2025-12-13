package com.peekr.presentation.friend.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.peekr.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.peekr.presentation.R
import com.peekr.presentation.friend.model.UiFriendInfo
import kotlinx.coroutines.flow.flowOf

@Composable
fun FriendsListScreen(
    modifier: Modifier = Modifier,
    friends: LazyPagingItems<UiFriendInfo>,
    onBackPress: () -> Unit,
) {
    FriendsListFrame(
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
            Contents(
                modifier = Modifier.fillMaxSize(),
                friends = friends,
                onFriendClick = {},
            )
        },
    )
}

@Composable
private fun FriendsListFrame(
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
 * 메인 컨텐츠
 *
 * @param modifier [Modifier]
 * @param friends 친구 목록
 */
@Composable
private fun Contents(
    modifier: Modifier = Modifier,
    friends: LazyPagingItems<UiFriendInfo>,
    onFriendClick: (UiFriendInfo) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 16.dp),
    ) {
        items(
            count = friends.itemCount,
            key = friends.itemKey { it.id },
        ) { idx ->
            val friend = friends[idx]
            friend?.let {
                FriendCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickableSingle(onClick = { onFriendClick(friend) })
                        .padding(horizontal = ScreenTokens.HorizontalPadding),
                    profileImageUrl = friend.profileImageUrl,
                    name = friend.name,
                    displayId = friend.displayId,
                )
            }
        }

        item {
            if (
                friends.loadState.append is LoadState.Loading ||
                friends.loadState.refresh is LoadState.Loading
            ) {
                FriendCardSkeleton(Modifier.fillMaxWidth())
            }
        }
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
        modifier = modifier.padding(vertical = 10.dp),
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
private fun ContentsPreview() {
    val friends = testFriendsPagingData.collectAsLazyPagingItems()

    PeekrAppTheme {
        Contents(
            modifier = Modifier.fillMaxSize(),
            friends = friends,
            onFriendClick = {},
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun FriendsListScreenPreview() {
    val friends = testFriendsPagingData.collectAsLazyPagingItems()

    PeekrAppTheme {
        FriendsListScreen(
            modifier = Modifier.fillMaxSize(),
            friends = friends,
            onBackPress = {},
        )
    }
}

private val testFriendsPagingData = flowOf(
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
