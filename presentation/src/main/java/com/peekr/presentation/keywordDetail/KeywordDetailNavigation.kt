package com.peekr.presentation.keywordDetail

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.peekr.core.presentation.common.navigation.Screens
import com.peekr.core.presentation.common.navigation.navigateToReport

fun NavGraphBuilder.keywordDetailNavigation(navController: NavController) {
    composable<Screens.KeywordDetail> { navBackStackEntry ->
        KeywordDetailRoute(
            onNavigateToReport = { userId, userKeywordId ->
                navController.navigateToReport(userId, userKeywordId)
            },
            onBackPressed = {
                navController.popBackStack()
            },
        )
    }
}
