package com.peekr.presentation.keywordDetail

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.dialog
import com.peekr.core.presentation.common.navigation.Screens

fun NavGraphBuilder.keywordDetailNavigation(navController: NavController) {
    dialog<Screens.KeywordDetail> { navBackStackEntry ->
        KeywordDetailRoute(
            onCancel = { navController.popBackStack() },
        )
    }
}
