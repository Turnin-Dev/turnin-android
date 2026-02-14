package com.peekr.presentation.block

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navigation
import com.peekr.core.presentation.common.navigation.SubGraph

fun NavGraphBuilder.blockNavigation(
    navController: NavHostController,
) {
    navigation<SubGraph.Block.Root>(startDestination = SubGraph.Block.SelectBlockReason) {
        dialog<SubGraph.Block.SelectBlockReason> { backStackEntry ->
        }

        dialog<SubGraph.Block.InputBlockReason> { backStackEntry ->
        }

        dialog<SubGraph.Block.BlockResult> { backStackEntry ->
        }
    }
}
