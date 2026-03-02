package com.peekr.presentation.setting

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.peekr.core.presentation.common.navigation.SubGraph

fun NavGraphBuilder.settingNavigation(
    navController: NavHostController,
) {
    navigation<SubGraph.Setting.Root>(startDestination = SubGraph.Setting.Main) {
        composable<SubGraph.Setting.Main> {
            SettingRoute(
                onBackPressed = { navController.popBackStack() },
            )
        }
    }
}
