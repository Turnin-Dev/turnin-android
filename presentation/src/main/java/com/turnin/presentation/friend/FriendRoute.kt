package com.turnin.presentation.friend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.turnin.core.designsystem.theme.TurninTheme
import com.turnin.core.presentation.common.navigation.args.UserProfileArgs
import com.turnin.core.presentation.common.util.ObserveAsEvents
import com.turnin.presentation.friend.state.FriendEffect
import com.turnin.presentation.friend.view.FriendListScreen
import com.turnin.presentation.friend.viewmodel.FriendListViewModel

@Composable
fun FriendRoute(
    onBackPressed: () -> Unit,
    onNavigateToUserProfile: (args: UserProfileArgs) -> Unit,
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
                onNavigateToUserProfile(effect.args)
            }

            FriendEffect.NavigateToMyProfile -> {
                onNavigateToMyProfile()
            }
        }
    }

    FriendListScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(TurninTheme.colorScheme.backgroundNormal),
        isMyFriendList = isMyFriendList,
        friends = friends,
        requesters = requesters,
        requestersStatus = requestersStatus,
        resetFriendsCache = viewModel::resetFriendsCache,
        resetRequestersCache = viewModel::resetRequestersCache,
        loadRequestersPagingData = viewModel::initRequestersPagingData,
        onFriendClick = viewModel::navigateToUserProfileOrMyProfile,
        onRequestAcceptClick = viewModel::acceptFriendRequest,
        onBackPressed = onBackPressed,
    )
}
