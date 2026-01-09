package com.peekr.presentation.keywordEdit

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.peekr.core.presentation.common.navigation.Screens

fun NavGraphBuilder.keywordEditNavigation(
    navController: NavHostController,
) {
    composable<Screens.KeywordEdit> {
        KeywordEditRoute(
            onBackPressed = {
                navController.popBackStack()
            },
        )
    }
}
