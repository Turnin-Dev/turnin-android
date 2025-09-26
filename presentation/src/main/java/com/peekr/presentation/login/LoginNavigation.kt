package com.peekr.presentation.login

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.peekr.core.presentation.navigation.LoginGraph
import com.peekr.core.presentation.navigation.Screens
import com.peekr.core.presentation.navigation.SubGraph

fun NavGraphBuilder.loginNavigation(navController: NavHostController) {
    navigation<SubGraph.Login>(startDestination = LoginGraph.Main) {
        composable<LoginGraph.Main> {
            // TODO: 메인 혹은 로그인 화면으로 이동 시 적절한 백스택 전략 적용 필요
            LoginRoute(
                modifier = Modifier,
                onNavigateMain = {
                    navController.navigate(Screens.TempMain)
                },
                onNavigateRegister = { provider, providerId ->
                    navController.navigate(SubGraph.Register(provider, providerId))
                },
            )
        }
    }
}
