package com.peekr.presentation.profile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.peekr.core.presentation.common.navigation.Screens
import com.peekr.core.presentation.common.navigation.navigateToReport
import com.peekr.presentation.profile.route.UserProfileRoute

fun NavGraphBuilder.userProfileNavigation(
    navController: NavController,
) {
    composable<Screens.UserProfile> {
        UserProfileRoute(
            onBackPressed = {
                navController.popBackStack()
            },
            navigateToReport = { reportedId ->
                navController.navigateToReport(reportedId)
            },
        )
    }
}
