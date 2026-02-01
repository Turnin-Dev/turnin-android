package com.peekr.peekrapp.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.peekr.core.presentation.common.navigation.SubGraph
import com.peekr.core.presentation.common.navigation.bottom.BottomNavigationFrame
import com.peekr.presentation.discover.main.DiscoverMainScreen
import com.peekr.presentation.home.homeNavigation
import com.peekr.presentation.profile.myProfileNavigation

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
            startDestination = SubGraph.BottomNav.Home,
        ) {
            homeNavigation(appNavController)

            composable<SubGraph.BottomNav.Discover> {
                DiscoverMainScreen(modifier = Modifier.fillMaxSize())
            }

            myProfileNavigation(appNavController)
        }
    }
}
