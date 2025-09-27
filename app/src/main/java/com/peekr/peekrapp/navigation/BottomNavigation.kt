package com.peekr.peekrapp.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.peekr.core.presentation.navigation.SubGraph
import com.peekr.presentation.discover.main.DiscoverMainScreen
import com.peekr.presentation.home.main.HomeMainScreen
import com.peekr.presentation.profile.view.ProfileMainScreen

fun NavGraphBuilder.bottomNavigation(bottomNavController: NavHostController) {
    composable<SubGraph.Home> {
        HomeMainScreen(
            modifier = Modifier.fillMaxSize(),
            bottomNavController = bottomNavController,
            onNavigateToSecond = {
                bottomNavController.navigate("HomeSecond")
            },
        )
    }

    composable<SubGraph.Discover> {
        DiscoverMainScreen(
            modifier = Modifier.fillMaxSize(),
            bottomNavController = bottomNavController,
        )
    }

    composable<SubGraph.Profile> {
        ProfileMainScreen(
            modifier = Modifier.fillMaxSize(),
            bottomNavController = bottomNavController,
        )
    }
}
