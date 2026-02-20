package com.peekr.presentation.keywordEdit

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.peekr.core.presentation.common.navigation.Screens

fun NavGraphBuilder.keywordEditNavigation(
    navController: NavHostController,
) {
    composable<Screens.KeywordEdit>(
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        popEnterTransition = enterTransition,
        popExitTransition = exitTransition,
    ) {
        KeywordEditRoute(
            onBackPressed = {
                navController.popBackStack()
            },
        )
    }
}

private val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition? =
    {
        slideIntoContainer(SlideDirection.Up, tween(300))
    }

private val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition? =
    {
        slideOutOfContainer(SlideDirection.Down, tween(300))
    }
