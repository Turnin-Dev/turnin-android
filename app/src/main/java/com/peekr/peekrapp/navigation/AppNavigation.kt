package com.peekr.peekrapp.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.peekr.presentation.login.loginNavigation
import com.peekr.presentation.register.registerNavigation
import com.peekr.presentation.shared.Screens
import com.peekr.presentation.shared.SubGraph
import com.peekr.presentation.shared.bottom.navigation.bottomNavigation

/**
 * Peekr의 앱 네비게이션
 */
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    appNavController: NavHostController,
) {
    NavHost(
        modifier = modifier,
        navController = appNavController,
        startDestination = SubGraph.Login,
    ) {
        loginNavigation(navController = appNavController)

        registerNavigation(navController = appNavController)

        bottomNavigation(bottomNavController = appNavController)

        composable<Screens.TempMain> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text("Main Screen", fontSize = 50.sp)
            }
        }
    }
}
