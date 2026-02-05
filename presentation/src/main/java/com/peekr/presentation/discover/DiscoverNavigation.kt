package com.peekr.presentation.discover

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.peekr.core.presentation.common.navigation.SubGraph
import com.peekr.core.presentation.common.navigation.navigateToKeywordDetail
import com.peekr.core.presentation.common.navigation.navigateToMyProfile
import com.peekr.core.presentation.common.navigation.navigateToUserProfile

fun NavGraphBuilder.discoverNavigation(appNavController: NavHostController) {
    composable<SubGraph.BottomNav.Discover> {
        DiscoverRoute(
            navigateToKeywordDetail = { userId, userKeywordId ->
                appNavController.navigateToKeywordDetail(userId, userKeywordId)
            },
            navigateToUserProfile = { userId ->
                appNavController.navigateToUserProfile(userId)
            },
            navigateToMyProfile = {
                appNavController.navigateToMyProfile()
            },
        )
    }
}
