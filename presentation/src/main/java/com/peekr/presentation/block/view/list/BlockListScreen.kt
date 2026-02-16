package com.peekr.presentation.block.view.list

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
import com.peekr.core.presentation.ui.component.card.ProfileCard
import com.peekr.core.presentation.ui.component.error.FooterError
import com.peekr.core.presentation.ui.component.lazycolumn.RefreshableLazyColumn
import com.peekr.core.presentation.ui.component.lazycolumn.pagingItem
import com.peekr.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.peekr.presentation.R
import com.peekr.presentation.block.model.UiBlockUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * 차단 목록 화면 프레임
 *
 * @param modifier [Modifier]
 * @param topBar 탑바
 * @param blockUsers 차단 사용자 목록
 */
@Composable
private fun BlockListFrame(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit,
    blockUsers: @Composable () -> Unit,
) {
    Column(modifier) {
        topBar()
        blockUsers()
    }
}

/**
 * 차단 목록 화면
 *
 * @param modifier [Modifier]
 * @param blockUsers 차단 사용자 목록
 * @param onBlockUserClick 차단 항목 클릭 시 콜백
 * @param onDelete 차단 해제(삭제) 시 콜백
 */
@Composable
fun BlockListScreen(
    modifier: Modifier = Modifier,
    blockUsers: LazyPagingItems<UiBlockUser>,
    onBlockUserClick: (UiBlockUser) -> Unit,
    onDelete: (UiBlockUser) -> Unit,
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
        blockUsers = {
            BlockList(
                modifier = Modifier.fillMaxSize(),
                blockUsers = blockUsers,
                onBlockUserClick = { },
                onDelete = {},
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
 * @param blockUsers 차단 사용자 목록
 * @param onBlockUserClick 차단 항목 클릭 시 콜백
 */
@Composable
private fun BlockList(
    modifier: Modifier = Modifier,
    blockUsers: LazyPagingItems<UiBlockUser>,
    onBlockUserClick: (UiBlockUser) -> Unit,
    onDelete: (UiBlockUser) -> Unit,
) {
    val scope = rememberCoroutineScope()
    var isRefreshing by rememberSaveable { mutableStateOf(false) }

    RefreshableLazyColumn(
        modifier = modifier,
        isRefreshing = isRefreshing,
        onRefresh = {
            scope.launch {
                isRefreshing = true
                blockUsers.refresh()
                isRefreshing = false
            }
        },
        contentPadding = ListContentPadding,
    ) {
        pagingItem(
            pagingItems = blockUsers,
            key = blockUsers.itemKey { it.id },
            skeletonCount = 20,
            skeleton = {
                BlockCardSkeleton()
            },
            initialError = {
                FooterError(
                    modifier = Modifier.fillMaxWidth(),
                    errorMessage = stringResource(R.string.block_list_screen_error_message_default),
                    onRetry = { blockUsers.retry() },
                )
            },
            footerError = {
                FooterError(
                    modifier = Modifier.fillMaxWidth(),
                    errorMessage = stringResource(R.string.block_list_screen_error_message_default),
                    onRetry = { blockUsers.retry() },
                )
            },
        ) { idx ->
            val block = blockUsers[idx]
            block?.let {
                BlockCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickableSingle(onClick = { onBlockUserClick(block) })
                        .padding(horizontal = ScreenTokens.HorizontalPadding),
                    profileImageUrl = it.profileImageUrl,
                    name = it.name,
                    displayId = it.displayId,
                    onDelete = { onDelete(block) },
                )
            }
        }
    }
}

/**
 * 차단 항목
 *
 * @param profileImageUrl 친구 프로필 사진 url
 * @param name 친구 이름
 * @param displayId 친구 사용자 표시 ID
 */
@Composable
private fun BlockCard(
    modifier: Modifier = Modifier,
    profileImageUrl: String?,
    name: String,
    displayId: String,
    onDelete: () -> Unit,
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
            onClick = onDelete,
        )
    }
}

/**
 * 차단 항목 스켈레톤
 */
@Composable
private fun BlockCardSkeleton(modifier: Modifier = Modifier) {
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
private val ListContentPadding = PaddingValues(top = 10.dp, bottom = 16.dp)

// ------------------------------ Previews ------------------------------
@PreviewLightDarkWithBackground
@Composable
private fun BlockCardPreview() {
    PeekrAppTheme {
        BlockCard(
            modifier = Modifier.fillMaxWidth(),
            profileImageUrl = "https://example.com/photo.jpg",
            name = "John Doe",
            displayId = "johndoe123",
            onDelete = {},
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun BlockListScreenPreview() {
    val blockUsers = testBlockUsersPagingData.collectAsLazyPagingItems()

    PeekrAppTheme {
        BlockListScreen(
            modifier = Modifier.fillMaxSize(),
            blockUsers = blockUsers,
            onBlockUserClick = {},
            onDelete = {},
            onBackPressed = {},
        )
    }
}

private val testBlockUsersPagingData = MutableStateFlow(
    PagingData.from(
        List(30) {
            UiBlockUser(
                id = it.toLong(),
                userId = it.toLong(),
                displayId = "DisplayID $it",
                name = "Name $it",
                profileImageUrl = null,
            )
        },
    ),
)
