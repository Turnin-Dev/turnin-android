package com.peekr.peekrapp.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.peekr.core.presentation.common.navigation.SubGraph
import com.peekr.core.presentation.common.navigation.bottom.BottomNavigationFrame
import com.peekr.presentation.discover.discoverNavigation
import com.peekr.presentation.home.homeNavigation
import com.peekr.presentation.profile.myProfileNavigation

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
                .padding(innerPadding),
            navController = bottomNavController,
            startDestination = SubGraph.BottomNav.Home,
            // TODO: 테스트용 트랜지션
//            enterTransition = { enterTransition },
//            exitTransition = { exitTransition },
//            popEnterTransition = { enterTransition },
//            popExitTransition = { exitTransition },
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None },
        ) {
            homeNavigation(
                appNavController = appNavController,
                onCheckPermission = onCheckPermission,
            )

            discoverNavigation(appNavController)

            myProfileNavigation<SubGraph.BottomNav.Profile>(appNavController)
        }
    }
}

private val enterTransition = fadeIn(animationSpec = tween(150))
private val exitTransition = fadeOut(animationSpec = tween(150))
