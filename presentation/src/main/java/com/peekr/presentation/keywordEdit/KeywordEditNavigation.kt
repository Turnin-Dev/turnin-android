package com.peekr.presentation.keywordEdit

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.peekr.core.designsystem.theme.PeekrTransitionDirection
import com.peekr.core.designsystem.theme.PeekrTransitionObject
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

private val enterTransition = PeekrTransitionObject.slideIn(PeekrTransitionDirection.Bottom)

private val exitTransition = PeekrTransitionObject.slideOut(PeekrTransitionDirection.Bottom)

private val popEnterTransition = PeekrTransitionObject.slideIn(PeekrTransitionDirection.Bottom)

private val popExitTransition = PeekrTransitionObject.slideOut(PeekrTransitionDirection.Bottom)
