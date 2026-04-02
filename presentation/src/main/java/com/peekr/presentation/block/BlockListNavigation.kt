package com.peekr.presentation.block

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.peekr.core.presentation.common.navigation.Screens
import com.peekr.core.presentation.common.navigation.args.UserProfileArgs
import com.peekr.core.presentation.common.navigation.navigateToUserProfile

fun NavGraphBuilder.blockListScreen(
    navController: NavController,
) {
    composable<Screens.BlockList> {
        BlockListRoute(
            onNavigateToUserProfile = { blockedUser ->
                val args = UserProfileArgs(userId = blockedUser.userId, blockId = blockedUser.id)
                navController.navigateToUserProfile(args)
            },
            onBackPressed = {
                navController.popBackStack()
            },
        )
    }
}
