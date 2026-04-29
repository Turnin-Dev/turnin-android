package com.turnin.presentation.friend

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.turnin.core.presentation.common.navigation.Screens
import com.turnin.core.presentation.common.navigation.navigateToMyProfile
import com.turnin.core.presentation.common.navigation.navigateToUserProfile

fun NavGraphBuilder.friendsListScreen(appNavController: NavHostController) {
    composable<Screens.FriendList> {
        FriendRoute(
            onNavigateToMyProfile = {
                appNavController.navigateToMyProfile()
            },
            onNavigateToUserProfile = { args ->
                appNavController.navigateToUserProfile(args)
            },
            onBackPressed = {
                appNavController.popBackStack()
            },
        )
    }
}
