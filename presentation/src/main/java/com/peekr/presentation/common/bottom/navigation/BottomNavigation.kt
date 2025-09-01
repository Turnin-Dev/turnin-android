package com.peekr.presentation.common.bottom.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.peekr.presentation.common.DiscoverGraph
import com.peekr.presentation.common.HomeGraph
import com.peekr.presentation.common.ProfileGraph
import com.peekr.presentation.common.SubGraph
import com.peekr.presentation.discover.main.DiscoverMainScreen
import com.peekr.presentation.home.main.HomeMainScreen
import com.peekr.presentation.profile.main.ProfileMainScreen

fun NavGraphBuilder.bottomNavigation(bottomNavController: NavHostController) {
    navigation<SubGraph.Home>(startDestination = HomeGraph.Main) {
        composable<HomeGraph.Main> {
            HomeMainScreen(
                modifier = Modifier.fillMaxSize(),
                bottomNavController = bottomNavController,
                onNavigateToSecond = {
                    bottomNavController.navigate("HomeSecond")
                },
            )
        }

        composable(route = "HomeSecond") {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text("Home", fontSize = 50.sp)
            }
        }
    }

    navigation<SubGraph.Discover>(startDestination = DiscoverGraph.Main) {
        composable<DiscoverGraph.Main> {
            DiscoverMainScreen(
                modifier = Modifier.fillMaxSize(),
                bottomNavController = bottomNavController,
            )
        }
    }

    navigation<SubGraph.Profile>(startDestination = ProfileGraph.Main) {
        composable<ProfileGraph.Main> {
            ProfileMainScreen(
                modifier = Modifier.fillMaxSize(),
                bottomNavController = bottomNavController,
            )
        }
    }
}
