package com.turnin.presentation.login

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.turnin.core.presentation.common.navigation.SubGraph

fun NavGraphBuilder.loginNavigation(
    navController: NavHostController,
    navigateToMain: () -> Unit,
) {
    navigation<SubGraph.Login.Root>(startDestination = SubGraph.Login.Main) {
        composable<SubGraph.Login.Main> {
            // TODO: 메인 혹은 로그인 화면으로 이동 시 적절한 백스택 전략 적용 필요
            LoginRoute(
                modifier = Modifier,
                onNavigateMain = navigateToMain,
                onNavigateRegister = { provider, providerId ->
                    navController.navigate(
                        SubGraph.Register.Root(provider, providerId),
                    )
                },
            )
        }
    }
}
