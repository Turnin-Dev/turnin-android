package com.peekr.presentation.home

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.peekr.core.presentation.common.navigation.SubGraph
import com.peekr.core.presentation.common.navigation.navigateToKeywordDetail
import com.peekr.core.presentation.common.navigation.navigateToUserProfile

fun NavGraphBuilder.homeNavigation(
    appNavController: NavHostController,
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
            onNavigateToUserProfile = { userId ->
                appNavController.navigateToUserProfile(userId)
            },
        )
    }
}
