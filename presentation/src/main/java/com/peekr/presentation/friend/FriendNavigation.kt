package com.peekr.presentation.friend

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.peekr.core.presentation.common.navigation.Screens
import com.peekr.core.presentation.common.navigation.navigateToMyProfile
import com.peekr.core.presentation.common.navigation.navigateToUserProfile

fun NavGraphBuilder.friendsListScreen(appNavController: NavHostController) {
    composable<Screens.FriendList> {
        FriendRoute(
            onNavigateToMyProfile = {
                appNavController.navigateToMyProfile()
            },
            onNavigateToUserProfile = { userId ->
                appNavController.navigateToUserProfile(userId)
            },
            onBackPressed = {
                appNavController.popBackStack()
            },
        )
    }
}
