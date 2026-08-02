package com.turnin.app.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.turnin.core.designsystem.theme.TurninTheme
import com.turnin.core.presentation.common.navigation.SubGraph
import com.turnin.core.presentation.common.navigation.bottom.BottomNavigationFrame
import com.turnin.presentation.discover.discoverNavigation
import com.turnin.presentation.home.homeNavigation
import com.turnin.presentation.profile.myProfileNavigation

@Composable
fun BottomNavigation(
    appNavController: NavHostController,
    modifier: Modifier = Modifier,
    onCheckPermission: () -> Unit,
) {
    val bottomNavController = rememberNavController()

    BottomNavigationFrame(
        modifier = modifier,
        bottomNavController = bottomNavController,
    ) { innerPadding ->
        NavHost(
            modifier = Modifier
                .fillMaxSize()
                .background(TurninTheme.colorScheme.backgroundNormal)
                .padding(innerPadding),
            navController = bottomNavController,
            startDestination = SubGraph.BottomNav.Home,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { enterTransition },
            popExitTransition = { exitTransition },
        ) {
            homeNavigation(
                appNavController = appNavController,
                bottomNavController = bottomNavController,
                onCheckPermission = onCheckPermission,
            )

            discoverNavigation(appNavController)

            myProfileNavigation<SubGraph.BottomNav.Profile>(appNavController)
        }
    }
}

private val enterTransition = fadeIn(animationSpec = tween(150))
private val exitTransition = fadeOut(animationSpec = tween(150))
