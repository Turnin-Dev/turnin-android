package com.peekr.presentation.profile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.peekr.core.presentation.common.navigation.Screens
import com.peekr.core.presentation.common.navigation.navigateToKeywordDetail
import com.peekr.core.presentation.common.navigation.navigateToReport
import com.peekr.presentation.profile.route.UserProfileRoute

fun NavGraphBuilder.userProfileNavigation(
    bottomNavController: NavController,
    appNavController: NavController,
) {
    composable<Screens.UserProfile> {
        UserProfileRoute(
            onBackPressed = {
                bottomNavController.popBackStack()
            },
            navigateToReport = { reportedId ->
                bottomNavController.navigateToReport(reportedId)
            },
            navigateToKeywordDetail = { userId, userKeywordId ->
                appNavController.navigateToKeywordDetail(userId, userKeywordId)
            },
        )
    }
}
