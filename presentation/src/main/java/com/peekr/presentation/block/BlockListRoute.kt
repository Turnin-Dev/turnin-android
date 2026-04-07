package com.peekr.presentation.block

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.presentation.block.model.UiBlockedUser
import com.peekr.presentation.block.view.list.BlockListScreen
import com.peekr.presentation.block.viewmodel.BlockListViewModel

@Composable
fun BlockListRoute(
    onNavigateToUserProfile: (UiBlockedUser) -> Unit,
    onBackPressed: () -> Unit,
) {
    val viewModel: BlockListViewModel = hiltViewModel()
    val blockedUsersPagingData = viewModel.blockedUsersPagingData.collectAsLazyPagingItems()

    BlockListScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(PeekrTheme.colorScheme.backgroundNormal),
        blockedUsers = blockedUsersPagingData,
        onBlockedUserClick = { blockedUser ->
            onNavigateToUserProfile(blockedUser)
        },
        onUnblock = { blockedUser ->
            viewModel.unblock(blockId = blockedUser.id, userId = blockedUser.userId)
        },
        onBackPressed = onBackPressed,
    )
}
