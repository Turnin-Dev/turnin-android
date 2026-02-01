package com.peekr.presentation.keywordDetail

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.peekr.core.presentation.common.navigation.Screens
import com.peekr.core.presentation.common.navigation.navigateToKeywordEdit
import com.peekr.core.presentation.common.navigation.navigateToReport
import com.peekr.core.presentation.common.navigation.navigateToUserProfile

fun NavGraphBuilder.keywordDetailNavigation(appNavController: NavController) {
    composable<Screens.KeywordDetail> { navBackStackEntry ->
        KeywordDetailRoute(
            onNavigateToReport = { userId, userKeywordId ->
                appNavController.navigateToReport(userId, userKeywordId)
            },
            onNavigateToKeywordEdit = { userKeywordId ->
                appNavController.navigateToKeywordEdit(userKeywordId)
            },
            onNavigateToUserProfile = { userId ->
                appNavController.navigateToUserProfile(userId)
            },
            onBackPressed = {
                appNavController.popBackStack()
            },
        )
    }
}
