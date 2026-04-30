package com.turnin.presentation.profile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.turnin.core.presentation.common.navigation.Screens
import com.turnin.core.presentation.common.navigation.deepLink.DeepLink
import com.turnin.core.presentation.common.navigation.navigateToFriendsList
import com.turnin.core.presentation.common.navigation.navigateToKeywordDetail
import com.turnin.core.presentation.common.navigation.navigateToReport
import com.turnin.presentation.profile.route.UserProfileRoute

fun NavGraphBuilder.userProfileNavigation(
    appNavController: NavController,
) {
    composable<Screens.UserProfile>(
        deepLinks = listOf(
            navDeepLink {
                uriPattern = DeepLink.Pattern.PROFILE
            },
        ),
    ) {
        UserProfileRoute(
            onBackPressed = {
                appNavController.popBackStack()
            },
            navigateToReport = { reportedId ->
                appNavController.navigateToReport(reportedId, null, false)
            },
            navigateToKeywordDetail = { userId, userKeywordId ->
                appNavController.navigateToKeywordDetail(userId, userKeywordId)
            },
            navigateToFriendsList = { userId ->
                appNavController.navigateToFriendsList(userId)
            },
        )
    }
}
