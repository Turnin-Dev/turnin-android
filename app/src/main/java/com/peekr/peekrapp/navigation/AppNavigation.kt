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
import com.peekr.core.presentation.common.navigation.BottomNav
import com.peekr.core.presentation.common.navigation.Screens
import com.peekr.core.presentation.common.navigation.SubGraph
import com.peekr.presentation.login.loginNavigation
import com.peekr.presentation.register.registerNavigation

/**
 * Peekr의 앱 네비게이션
 */
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    appNavController: NavHostController,
) {
    // TODO login/Register Navigation 에서 bottomNavigation 으로 이동시 아래 코드와 같이 백스택을 확실히 클리어 해야 한다.
    // navController.navigate(SubGraph.Home) {
    //    popUpTo(0) { inclusive = true }
    //    launchSingleTop = true
    // }
    NavHost(
        modifier = modifier,
        navController = appNavController,
        startDestination = SubGraph.Login,
    ) {
        loginNavigation(navController = appNavController)

        registerNavigation(navController = appNavController)

        composable<BottomNav> {
            BottomNavigation(
                modifier = Modifier.fillMaxSize(),
                appNavController = appNavController,
            )
        }

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
