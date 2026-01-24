package com.peekr.presentation.friend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.paging.compose.collectAsLazyPagingItems
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.presentation.common.navigation.Screens
import com.peekr.core.presentation.common.navigation.navigateToUserProfile
import com.peekr.presentation.friend.view.FriendListScreen
import com.peekr.presentation.friend.viewmodel.FriendListViewModel

fun NavGraphBuilder.friendsListScreen(navController: NavHostController) {
    composable<Screens.FriendsList> {
        val friendListViewModel: FriendListViewModel = hiltViewModel()
        val friends = friendListViewModel.friendsPagingData.collectAsLazyPagingItems()

        FriendListScreen(
            modifier = Modifier
                .fillMaxSize()
                .background(PeekrTheme.colorScheme.backgroundNormal),
            friends = friends,
            onFriendClick = { uiFriendInfo ->
                navController.navigateToUserProfile(uiFriendInfo.userId)
            },
            onBackPress = {
                navController.popBackStack()
            },
        )
    }
}
