package com.peekr.presentation.friend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.presentation.common.util.ObserveAsEvents
import com.peekr.presentation.friend.state.FriendEffect
import com.peekr.presentation.friend.view.FriendListScreen
import com.peekr.presentation.friend.viewmodel.FriendListViewModel

@Composable
fun FriendRoute(
    onBackPressed: () -> Unit,
    onNavigateToUserProfile: (userId: Long) -> Unit,
    onNavigateToMyProfile: () -> Unit,
) {
    val viewModel: FriendListViewModel = hiltViewModel()
    val friends = viewModel.friendsPagingData.collectAsLazyPagingItems()
    val requesters = viewModel.requestersPagingData.collectAsLazyPagingItems()
    val requestersStatus by viewModel.requesterStatus.collectAsStateWithLifecycle()
    val isMyFriendList by viewModel.isMyFriendList.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.effect) { effect ->
        when (effect) {
            is FriendEffect.NavigateToUserProfile -> {
                onNavigateToUserProfile(effect.userId)
            }

            FriendEffect.NavigateToMyProfile -> {
                onNavigateToMyProfile()
            }
        }
    }

    FriendListScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(PeekrTheme.colorScheme.backgroundNormal),
        isMyFriendList = isMyFriendList,
        friends = friends,
        requesters = requesters,
        requestersStatus = requestersStatus,
        loadRequestersPagingData = {
            viewModel.initRequestersPagingData()
        },
        onFriendClick = { friendUserId ->
            viewModel.navigateToUserProfileOrMyProfile(friendUserId)
        },
        onRequestAcceptClick = { targetId, status ->
            viewModel.acceptFriendRequest(targetId, status)
        },
        onBackPressed = onBackPressed,
    )
}
