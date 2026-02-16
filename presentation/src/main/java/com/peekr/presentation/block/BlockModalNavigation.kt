package com.peekr.presentation.block

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navigation
import com.peekr.core.presentation.common.navigation.SubGraph
import com.peekr.core.presentation.common.util.ObserveAsEvents
import com.peekr.core.presentation.common.viewmodel.sharedViewModel
import com.peekr.presentation.block.state.BlockModalContract
import com.peekr.presentation.block.view.modal.BlockResultModal
import com.peekr.presentation.block.view.modal.InputBlockReasonModal
import com.peekr.presentation.block.view.modal.SelectBlockReasonModal
import com.peekr.presentation.block.viewmodel.BlockModalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 차단 모달 네비게이션
 *
 * 대부분 신고 네비게이션에서 넘어온다.
 *
 * @see SubGraph.Report
 */
@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.blockModalNavigation(
    navController: NavHostController,
) {
    navigation<SubGraph.BlockModal.Root>(startDestination = SubGraph.BlockModal.SelectBlockModalReason) {
        dialog<SubGraph.BlockModal.SelectBlockModalReason> { backStackEntry ->
            val viewModel: BlockModalViewModel =
                backStackEntry.sharedViewModel(navController, useHiltViewModel = true)
            val sheetState = rememberModalBottomSheetState()
            val scope = rememberCoroutineScope()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModel.processEvent(
                    BlockModalContract.UiEvent.GetBlockReasons,
                )
            }

            ObserveAsEvents(viewModel.effect) { effect ->
                if (effect is BlockModalContract.UiEffect.CloseBlockModal) {
                    exitBlockModalNavigation(scope, sheetState, navController)
                }
            }

            SelectBlockReasonModal(
                sheetState = sheetState,
                blockReasons = uiState.blockReasons,
                loading = uiState.loading,
                error = uiState.error,
                onDismissRequest = {
                    exitBlockModalNavigation(scope, sheetState, navController)
                },
                onCancel = { navController.popBackStack() },
                onBlockReasonClick = { blockReason ->
                    viewModel.processEvent(
                        BlockModalContract.UiEvent.SelectBlockReason(blockReason),
                    )
                    navController.navigate(SubGraph.BlockModal.InputBlockModalReason) {
                        popUpTo(SubGraph.BlockModal.SelectBlockModalReason) {
                            inclusive = true
                        }
                    }
                },
            )
        }

        dialog<SubGraph.BlockModal.InputBlockModalReason> { backStackEntry ->
            val viewModel: BlockModalViewModel =
                backStackEntry.sharedViewModel(navController, useHiltViewModel = true)
            val sheetState = rememberModalBottomSheetState()
            val scope = rememberCoroutineScope()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            ObserveAsEvents(viewModel.effect) { effect ->
                when (effect) {
                    BlockModalContract.UiEffect.NavigateToBlockResult -> {
                        navController.navigate(SubGraph.BlockModal.BlockModalResult) {
                            popUpTo(SubGraph.BlockModal.InputBlockModalReason) {
                                inclusive = true
                            }
                        }
                    }

                    BlockModalContract.UiEffect.CloseBlockModal -> {
                        exitBlockModalNavigation(scope, sheetState, navController)
                    }
                }
            }

            InputBlockReasonModal(
                sheetState = sheetState,
                loading = uiState.loading,
                onDismissRequest = {
                    exitBlockModalNavigation(scope, sheetState, navController)
                },
                onBlock = { reason ->
                    viewModel.processEvent(
                        BlockModalContract.UiEvent.OnBlock(reason),
                    )
                },
            )
        }

        dialog<SubGraph.BlockModal.BlockModalResult> { backStackEntry ->
            val viewModel: BlockModalViewModel =
                backStackEntry.sharedViewModel(navController, useHiltViewModel = true)
            val sheetState = rememberModalBottomSheetState()
            val scope = rememberCoroutineScope()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            BlockResultModal(
                sheetState = sheetState,
                error = uiState.error,
                onDismissRequest = {
                    exitBlockModalNavigation(scope, sheetState, navController)
                },
                onFinishClick = {
                    exitBlockModalNavigation(scope, sheetState, navController)
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private fun exitBlockModalNavigation(
    scope: CoroutineScope,
    sheetState: SheetState,
    navController: NavHostController,
) {
    scope.launch {
        sheetState.hide()
        navController.popBackStack<SubGraph.BlockModal.Root>(inclusive = true)
    }
}
