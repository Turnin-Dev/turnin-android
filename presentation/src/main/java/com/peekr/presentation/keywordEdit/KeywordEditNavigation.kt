package com.peekr.presentation.keywordEdit

import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.peekr.core.presentation.common.navigation.Screens

fun NavGraphBuilder.keywordEditNavigation(
    navController: NavHostController,
) {
    composable<Screens.KeywordEdit>(
        enterTransition = { enterTransition },
        exitTransition = { exitTransition },
        popEnterTransition = { popEnterTransition },
        popExitTransition = { popExitTransition },
    ) {
        KeywordEditRoute(
            onBackPressed = {
                navController.popBackStack()
            },
        )
    }
}

private val enterTransition = slideInVertically(
    initialOffsetY = { it },
)

private val exitTransition = slideOutVertically(
    targetOffsetY = { it },
)

private val popEnterTransition = slideInVertically(
    initialOffsetY = { it },
)

private val popExitTransition = slideOutVertically(
    targetOffsetY = { it },
)
