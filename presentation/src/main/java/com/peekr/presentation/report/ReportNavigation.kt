package com.peekr.presentation.report

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
import androidx.navigation.navigation
import com.peekr.core.presentation.common.navigation.SubGraph
import com.peekr.core.presentation.common.util.ObserveAsEvents
import com.peekr.core.presentation.common.viewmodel.sharedViewModel
import com.peekr.presentation.report.state.ReportContract
import com.peekr.presentation.report.view.InputReportReasonModal
import com.peekr.presentation.report.view.ReportResultModal
import com.peekr.presentation.report.view.SelectReportBlockModal
import com.peekr.presentation.report.view.SelectReportReasonModal
import com.peekr.presentation.report.viewmodel.ReportViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.reportNavigation(
    navController: NavHostController,
) {
    navigation<SubGraph.Report.Root>(startDestination = SubGraph.Report.SelectReportBlock) {
        dialog<SubGraph.Report.SelectReportBlock> { backStackEntry ->
            val viewModel: ReportViewModel =
                backStackEntry.sharedViewModel(navController, useHiltViewModel = true)
            val sheetState = rememberModalBottomSheetState()

            SelectReportBlockModal(
                sheetState = sheetState,
                onDismissRequest = { navController.popBackStack() },
                onCancel = { navController.popBackStack() },
                selectReport = {
                    navController.navigate(SubGraph.Report.SelectReportReason) {
                        popUpTo(SubGraph.Report.SelectReportBlock) {
                            inclusive = true
                        }
                    }
                },
                selectBlock = {},
            )
        }

        dialog<SubGraph.Report.SelectReportReason> { backStackEntry ->
            val viewModel: ReportViewModel =
                backStackEntry.sharedViewModel(navController, useHiltViewModel = true)
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val sheetState = rememberModalBottomSheetState()
            val scope = rememberCoroutineScope()

            LaunchedEffect(Unit) {
                viewModel.processEvent(
                    ReportContract.UiEvent.GetReportReasons,
                )
            }

            ObserveAsEvents(viewModel.effect) { effect ->
                if (effect is ReportContract.UiEffect.CloseReportModal) {
                    exitReportNavigation(
                        scope = scope,
                        sheetState = sheetState,
                        navController = navController,
                    )
                }
            }

            SelectReportReasonModal(
                sheetState = sheetState,
                reportReasons = uiState.reportReasons,
                loading = uiState.loading,
                onDismissRequest = { navController.popBackStack() },
                onCancel = { navController.popBackStack() },
                onReportReasonsClick = { reportReason ->
                    viewModel.processEvent(
                        ReportContract.UiEvent.SelectReportReason(reportReason),
                    )
                    navController.navigate(SubGraph.Report.InputReportReason) {
                        popUpTo(SubGraph.Report.SelectReportReason) {
                            inclusive = true
                        }
                    }
                },
            )
        }

        dialog<SubGraph.Report.InputReportReason> { backStackEntry ->
            val viewModel: ReportViewModel =
                backStackEntry.sharedViewModel(navController, useHiltViewModel = true)
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val sheetState = rememberModalBottomSheetState()
            val scope = rememberCoroutineScope()

            ObserveAsEvents(viewModel.effect) { effect ->
                when (effect) {
                    ReportContract.UiEffect.NavigateToReportResult -> {
                        navController.navigate(SubGraph.Report.ReportResult) {
                            popUpTo(SubGraph.Report.InputReportReason) {
                                inclusive = true
                            }
                        }
                    }

                    ReportContract.UiEffect.CloseReportModal -> {
                        exitReportNavigation(
                            scope = scope,
                            sheetState = sheetState,
                            navController = navController,
                        )
                    }
                }
            }

            InputReportReasonModal(
                sheetState = sheetState,
                loading = uiState.loading,
                onDismissRequest = { navController.popBackStack() },
                onCancel = { navController.popBackStack() },
                onReport = { reason ->
                    viewModel.processEvent(
                        ReportContract.UiEvent.OnReport(reason),
                    )
                },
            )
        }

        dialog<SubGraph.Report.ReportResult> { backStackEntry ->
            val viewModel: ReportViewModel =
                backStackEntry.sharedViewModel(navController, useHiltViewModel = true)
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val sheetState = rememberModalBottomSheetState()
            val scope = rememberCoroutineScope()

            uiState.reportResult?.let { isSuccess ->
                ReportResultModal(
                    sheetState = sheetState,
                    isSuccess = isSuccess,
                    onDismissRequest = { navController.popBackStack() },
                    onCancel = { navController.popBackStack() },
                    onFinishClick = {
                        exitReportNavigation(
                            scope = scope,
                            sheetState = sheetState,
                            navController = navController,
                        )
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private fun exitReportNavigation(
    scope: CoroutineScope,
    sheetState: SheetState,
    navController: NavHostController,
) {
    scope.launch {
        sheetState.hide()
        navController.popBackStack<SubGraph.Report.Root>(inclusive = true)
    }
}
