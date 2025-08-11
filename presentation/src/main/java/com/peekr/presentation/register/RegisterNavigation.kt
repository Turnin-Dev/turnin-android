package com.peekr.presentation.register

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.peekr.presentation.shared.RegisterGraph
import com.peekr.presentation.shared.SubGraph

fun NavGraphBuilder.registerNavigation(navController: NavHostController) {
    navigation<SubGraph.Register>(startDestination = RegisterGraph.Name) {
        composable<RegisterGraph.Name> {
        }

        composable<RegisterGraph.Nickname> {
        }

        composable<RegisterGraph.Profile> {
        }
    }
}
