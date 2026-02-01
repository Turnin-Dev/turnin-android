package com.peekr.presentation.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.peekr.core.presentation.common.navigation.SubGraph
import com.peekr.core.presentation.common.navigation.navigateToKeywordDetail

fun NavGraphBuilder.homeNavigation(
    bottomNavController: NavHostController,
    appNavController: NavHostController,
) {
    composable<SubGraph.BottomNav.Home> {
        HomeRoute(
            onNavigateToKeywordDetail = { userId, userKeywordId ->
                appNavController.navigateToKeywordDetail(userId, userKeywordId)
            },
        )
    }
}
