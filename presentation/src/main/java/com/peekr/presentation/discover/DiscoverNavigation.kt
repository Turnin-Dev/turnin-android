package com.peekr.presentation.discover

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.peekr.core.presentation.common.navigation.SubGraph

fun NavGraphBuilder.discoverNavigation(appNavController: NavHostController) {
    composable<SubGraph.BottomNav.Discover> {
        DiscoverRoute()
    }
}
