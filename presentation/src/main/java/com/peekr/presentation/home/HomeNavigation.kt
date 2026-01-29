package com.peekr.presentation.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.peekr.core.presentation.common.navigation.SubGraph

fun NavGraphBuilder.homeNavigation(
    navController: NavHostController,
) {
    composable<SubGraph.BottomNav.Home> {
        // Home Route
    }
}
