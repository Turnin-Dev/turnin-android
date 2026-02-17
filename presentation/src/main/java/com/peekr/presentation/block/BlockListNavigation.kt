package com.peekr.presentation.block

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.peekr.core.presentation.common.navigation.Screens

fun NavGraphBuilder.blockListScreen(
    navController: NavController,
) {
    composable<Screens.BlockList> {
        BlockListRoute(
            onBackPressed = {
                navController.popBackStack()
            },
        )
    }
}
