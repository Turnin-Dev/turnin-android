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
import com.peekr.core.presentation.common.navigation.Screens
import com.peekr.core.presentation.common.navigation.SubGraph
import com.peekr.presentation.keywordDetail.keywordDetailNavigation
import com.peekr.presentation.keywordEdit.keywordEditNavigation
import com.peekr.presentation.login.loginNavigation
import com.peekr.presentation.register.registerNavigation
import com.peekr.presentation.report.reportNavigation

/**
 * Peekr의 앱 네비게이션
 */
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    appNavController: NavHostController,
    loggedIn: Boolean?,
) {
    if (loggedIn != null) {
        NavHost(
            modifier = modifier,
            navController = appNavController,
            startDestination = if (loggedIn) {
                SubGraph.BottomNav.Root
            } else {
                SubGraph.Login.Root
            },
        ) {
            loginNavigation(
                navController = appNavController,
                navigateToMain = {
                    appNavController.navigate(SubGraph.BottomNav.Root) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )

            registerNavigation(
                navController = appNavController,
                navigateToMain = {
                    appNavController.navigate(SubGraph.BottomNav.Root) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )

            composable<SubGraph.BottomNav.Root> {
                BottomNavigation(
                    modifier = Modifier.fillMaxSize(),
                    appNavController = appNavController,
                )
            }

            keywordDetailNavigation(appNavController)

            keywordEditNavigation(appNavController)

            reportNavigation(
                navController = appNavController,
            )

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
}
