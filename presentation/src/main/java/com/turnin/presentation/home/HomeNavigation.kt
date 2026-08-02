package com.turnin.presentation.home

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.turnin.core.presentation.common.navigation.SubGraph
import com.turnin.core.presentation.common.navigation.navigateToBottomBarItem
import com.turnin.core.presentation.common.navigation.navigateToKeywordDetail
import com.turnin.core.presentation.common.navigation.navigateToNotification
import com.turnin.core.presentation.common.navigation.navigateToUserProfile

fun NavGraphBuilder.homeNavigation(
    appNavController: NavHostController,
    bottomNavController: NavHostController,
    onCheckPermission: () -> Unit,
) {
    composable<SubGraph.BottomNav.Home> {
        LaunchedEffect(Unit) {
            onCheckPermission()
        }

        HomeRoute(
            onNavigateToKeywordDetail = { userId, userKeywordId ->
                appNavController.navigateToKeywordDetail(userId, userKeywordId)
            },
            onNavigateToUserProfile = { args ->
                appNavController.navigateToUserProfile(args)
            },
            onNavigateToNotification = {
                appNavController.navigateToNotification()
            },
            onNavigateToDiscover = {
                bottomNavController.navigateToBottomBarItem(SubGraph.BottomNav.Discover)
            },
        )
    }
}
