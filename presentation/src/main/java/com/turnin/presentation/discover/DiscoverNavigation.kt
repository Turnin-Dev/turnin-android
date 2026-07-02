package com.turnin.presentation.discover

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.turnin.core.presentation.common.navigation.SubGraph
import com.turnin.core.presentation.common.navigation.navigateToKeywordDetail
import com.turnin.core.presentation.common.navigation.navigateToMyProfile
import com.turnin.core.presentation.common.navigation.navigateToUserProfile

fun NavGraphBuilder.discoverNavigation(appNavController: NavHostController) {
    composable<SubGraph.BottomNav.Discover> {
        DiscoverRoute(
            navigateToKeywordDetail = { userId, userKeywordId ->
                appNavController.navigateToKeywordDetail(userId, userKeywordId)
            },
            navigateToUserProfile = { args ->
                appNavController.navigateToUserProfile(args)
            },
            navigateToMyProfile = {
                appNavController.navigateToMyProfile()
            },
        )
    }
}
