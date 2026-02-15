package com.peekr.presentation.block

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navigation
import com.peekr.core.presentation.common.navigation.SubGraph

fun NavGraphBuilder.blockNavigation(
    navController: NavHostController,
) {
    navigation<SubGraph.BlockModal.Root>(startDestination = SubGraph.BlockModal.SelectBlockModalReason) {
        dialog<SubGraph.BlockModal.SelectBlockModalReason> { backStackEntry ->
        }

        dialog<SubGraph.BlockModal.InputBlockModalReason> { backStackEntry ->
        }

        dialog<SubGraph.BlockModal.BlockModalResult> { backStackEntry ->
        }
    }
}
