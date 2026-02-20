package com.peekr.presentation.block.view.list

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
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
import com.peekr.core.designsystem.component.button.PeekrButtonStyle
import com.peekr.core.designsystem.component.button.PeekrOutlinedButton
import com.peekr.core.designsystem.component.skeleton.SkeletonBox
import com.peekr.core.designsystem.component.topbar.PeekrTopBar
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.util.click.clickableSingle
import com.peekr.core.designsystem.util.token.ScreenTokens
import com.peekr.core.presentation.ui.component.EmptyGuidance
import com.peekr.core.presentation.ui.component.card.ProfileCard
import com.peekr.core.presentation.ui.component.error.FooterError
import com.peekr.core.presentation.ui.component.lazycolumn.RefreshableLazyColumn
import com.peekr.core.presentation.ui.component.lazycolumn.pagingItem
import com.peekr.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.peekr.presentation.R
import com.peekr.presentation.block.model.UiBlockedUser
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * 차단 목록 화면 프레임
 *
 * @param modifier [Modifier]
 * @param topBar 탑바
 * @param blockedUsers 차단 사용자 목록
 */
@Composable
private fun BlockListFrame(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit,
    blockedUsers: @Composable () -> Unit,
) {
    Column(modifier) {
        topBar()
        blockedUsers()
    }
}

/**
 * 차단 목록 화면
 *
 * @param modifier [Modifier]
 * @param blockedUsers 차단 사용자 목록
 * @param onBlockedUserClick 차단 사용자 클릭 시 콜백
 * @param onUnblock 차단 해제 시 콜백
 */
@Composable
fun BlockListScreen(
    modifier: Modifier = Modifier,
    blockedUsers: LazyPagingItems<UiBlockedUser>,
    onBlockedUserClick: (UiBlockedUser) -> Unit,
    onUnblock: (UiBlockedUser) -> Unit,
    onBackPressed: () -> Unit,
) {
    BlockListFrame(
        modifier = modifier,
        topBar = {
            TopBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ScreenTokens.HorizontalPaddingWithTouchTarget),
                onBackPressed = onBackPressed,
            )
        },
        blockedUsers = {
            BlockList(
                modifier = Modifier.fillMaxSize(),
                blockedUsers = blockedUsers,
                onBlockedUserClick = { blockedUser ->
                    onBlockedUserClick(blockedUser)
                },
                onUnblock = { blockedUser ->
                    onUnblock(blockedUser)
                },
            )
        },
    )
}

/**
 * 탑바
 *
 * @param modifier [Modifier]
 * @param onBackPressed 뒤로 가기 시 콜백
 */
@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
) {
    PeekrTopBar(
        modifier = modifier,
        title = stringResource(R.string.block_list_screen_top_bar_title),
        onBackPressed = onBackPressed,
    )
}

/**
 * 차단 목록
 *
 * @param modifier [Modifier]
 * @param blockedUsers 차단 사용자 목록
 * @param onBlockedUserClick 차단 사용자 클릭 시 콜백
 * @param onUnblock 차단 해제 시 콜백
 */
@Composable
private fun BlockList(
    modifier: Modifier = Modifier,
    blockedUsers: LazyPagingItems<UiBlockedUser>,
    onBlockedUserClick: (UiBlockedUser) -> Unit,
    onUnblock: (UiBlockedUser) -> Unit,
) {
    val isRefreshing = remember {
        derivedStateOf {
            blockedUsers.loadState.refresh is LoadState.Loading
        }
    }.value

    RefreshableLazyColumn(
        modifier = modifier,
        isRefreshing = isRefreshing,
        onRefresh = { blockedUsers.refresh() },
        contentPadding = ListContentPadding,
    ) {
        pagingItem(
            pagingItems = blockedUsers,
            key = blockedUsers.itemKey { it.id },
            skeletonCount = 20,
            skeleton = {
                BlockedUserCardSkeleton()
            },
            initialError = {
                FooterError(
                    modifier = Modifier.fillMaxWidth(),
                    errorMessage = stringResource(R.string.block_list_screen_error_message_default),
                    onRetry = { blockedUsers.retry() },
                )
            },
            footerError = {
                FooterError(
                    modifier = Modifier.fillMaxWidth(),
                    errorMessage = stringResource(R.string.block_list_screen_error_message_default),
                    onRetry = { blockedUsers.retry() },
                )
            },
            emptyGuidance = {
                BlocksEmptyGuidance()
            },
        ) { idx ->
            val block = blockedUsers[idx]
            block?.let {
                BlockedUserCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickableSingle(onClick = { onBlockedUserClick(block) })
                        .animateItem(
                            fadeInSpec = null,
                            fadeOutSpec = tween(300),
                        )
                        .padding(horizontal = ScreenTokens.HorizontalPadding),
                    profileImageUrl = it.profileImageUrl,
                    name = it.name,
                    displayId = it.displayId,
                    loading = it.loading,
                    onUnblock = { onUnblock(block) },
                )
            }
        }
    }
}

/**
 * 차단 사용자 항목
 *
 * @param modifier [Modifier]
 * @param profileImageUrl 프로필 사진 url
 * @param name 이름
 * @param displayId 사용자 표시 ID
 * @param loading 로딩 여부
 * @param onUnblock 차단 해제 시 콜백
 */
@Composable
private fun BlockedUserCard(
    modifier: Modifier = Modifier,
    profileImageUrl: String?,
    name: String,
    displayId: String,
    loading: Boolean,
    onUnblock: () -> Unit,
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

        PeekrOutlinedButton(
            text = stringResource(R.string.block_list_screen_btn_delete),
            style = PeekrButtonStyle.Tiny,
            loading = loading,
            onClick = onUnblock,
        )
    }
}

/**
 * 차단 항목 스켈레톤
 */
@Composable
private fun BlockedUserCardSkeleton(modifier: Modifier = Modifier) {
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

/**
 * 차단 목록 빈 화면 안내 뷰
 */
@Composable
private fun BlocksEmptyGuidance() {
    EmptyGuidance(title = stringResource(R.string.block_list_screen_empty_guidance_title))
}

private val AvatarSize = 58.dp
private val ListContentPadding = PaddingValues(top = 10.dp, bottom = 16.dp)

// ------------------------------ Previews ------------------------------
@PreviewLightDarkWithBackground
@Composable
private fun BlockedUserCardPreview() {
    PeekrAppTheme {
        BlockedUserCard(
            modifier = Modifier.fillMaxWidth(),
            profileImageUrl = "https://example.com/photo.jpg",
            name = "John Doe",
            displayId = "johndoe123",
            loading = false,
            onUnblock = {},
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun BlockListScreenPreview() {
    val blockedUsers = testBlockedUsersPagingData.collectAsLazyPagingItems()

    PeekrAppTheme {
        BlockListScreen(
            modifier = Modifier.fillMaxSize(),
            blockedUsers = blockedUsers,
            onBlockedUserClick = {},
            onUnblock = {},
            onBackPressed = {},
        )
    }
}

private val testBlockedUsersPagingData = MutableStateFlow(
    PagingData.from(
        List(30) {
            UiBlockedUser(
                id = it.toLong(),
                userId = it.toLong(),
                displayId = "DisplayID $it",
                name = "Name $it",
                profileImageUrl = null,
            )
        },
    ),
)
