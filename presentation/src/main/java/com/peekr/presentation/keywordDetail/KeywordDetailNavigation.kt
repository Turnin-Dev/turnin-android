package com.peekr.presentation.keywordDetail

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.peekr.core.presentation.common.navigation.Screens

fun NavGraphBuilder.keywordDetailNavigation(navController: NavController) {
    composable<Screens.KeywordDetail> { navBackStackEntry ->
        KeywordDetailRoute()
    }
}
