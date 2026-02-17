package com.peekr.presentation.block

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.peekr.core.presentation.common.navigation.Screens
import com.peekr.core.presentation.common.navigation.navigateToUserProfile

fun NavGraphBuilder.blockListScreen(
    navController: NavController,
) {
    composable<Screens.BlockList> {
        BlockListRoute(
            onNavigateToUserProfile = { blockedUserId ->
                navController.navigateToUserProfile(blockedUserId)
            },
            onBackPressed = {
                navController.popBackStack()
            },
        )
    }
}
