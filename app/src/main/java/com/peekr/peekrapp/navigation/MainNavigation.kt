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
import androidx.navigation.navigation
import com.peekr.presentation.login.LoginRoute
import com.peekr.presentation.register.registerNavigation
import com.peekr.presentation.shared.LoginGraph
import com.peekr.presentation.shared.Screens
import com.peekr.presentation.shared.SubGraph

/**
 * Peekr의 메인 네비게이션
 */
@Composable
fun MainNavigation(
    modifier: Modifier = Modifier,
    mainNavController: NavHostController,
) {
    NavHost(
        modifier = modifier,
        navController = mainNavController,
        startDestination = SubGraph.Login,
    ) {
        navigation<SubGraph.Login>(startDestination = LoginGraph.Default) {
            composable<LoginGraph.Default> {
                // TODO: 메인 혹은 로그인 화면으로 이동 시 적절한 백스택 전략 적용 필요
                LoginRoute(
                    modifier = Modifier,
                    onNavigateMain = {
                        mainNavController.navigate(Screens.TempMain)
                    },
                    onNavigateRegister = {
                        mainNavController.navigate(SubGraph.Register)
                    },
                )
            }
        }

        registerNavigation(navController = mainNavController)

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
