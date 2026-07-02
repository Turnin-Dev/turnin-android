package com.turnin.presentation.login

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.turnin.core.presentation.common.navigation.SubGraph
import com.turnin.core.presentation.common.navigation.navigateToRegister

fun NavGraphBuilder.loginNavigation(
    appNavController: NavHostController,
) {
    navigation<SubGraph.Login.Root>(startDestination = SubGraph.Login.Main) {
        composable<SubGraph.Login.Main> {
            LoginRoute(
                modifier = Modifier,
                onNavigateMain = {
                    appNavController.navigate(SubGraph.BottomNav.Root) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateRegister = { provider, providerId ->
                    appNavController.navigateToRegister(provider, providerId)
                },
            )
        }
    }
}
