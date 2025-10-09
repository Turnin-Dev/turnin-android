package com.peekr.peekrapp.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.peekr.core.presentation.navigation.SubGraph
import com.peekr.core.presentation.navigation.bottom.BottomNavigationFrame
import com.peekr.presentation.discover.main.DiscoverMainScreen
import com.peekr.presentation.home.main.HomeMainScreen
import com.peekr.presentation.profile.profileNavigation

@Composable
fun BottomNavigation(
    appNavController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val bottomNavController = rememberNavController()

    BottomNavigationFrame(
        modifier = modifier,
        bottomNavController = bottomNavController,
    ) { innerPadding ->
        NavHost(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            navController = bottomNavController,
            startDestination = SubGraph.Home,
        ) {
            composable<SubGraph.Home> {
                HomeMainScreen(
                    modifier = Modifier.fillMaxSize(),
                    onNavigateToSecond = {
                        appNavController.navigate("HomeSecond")
                    },
                )
            }

            composable<SubGraph.Discover> {
                DiscoverMainScreen(modifier = Modifier.fillMaxSize())
            }

            profileNavigation()
        }
    }
}
