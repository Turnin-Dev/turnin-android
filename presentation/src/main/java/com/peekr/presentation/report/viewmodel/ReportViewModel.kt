package com.peekr.presentation.report.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.peekr.core.domain.common.Result
import com.peekr.core.presentation.common.snackbar.SnackbarController
import com.peekr.core.presentation.common.snackbar.SnackbarEvent
import com.peekr.core.presentation.common.viewmodel.MVIBaseViewModel
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.domain.report.error.ReportErrorType
import com.peekr.domain.report.usecase.GetReportReasonsUseCase
import com.peekr.domain.report.usecase.ReportUseCase
import com.peekr.presentation.report.error.asUiText
import com.peekr.presentation.report.model.UiReportReason
import com.peekr.presentation.report.model.toUiModel
import com.peekr.presentation.report.state.ReportContract
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val snackbarController: SnackbarController,
    private val reportUseCase: ReportUseCase,
    private val getReportReasonsUseCase: GetReportReasonsUseCase,
    savedStateHandle: SavedStateHandle,
) : MVIBaseViewModel<ReportContract.UiState, ReportContract.UiEvent, ReportContract.UiEffect>() {
    private val reportedId: Long by lazy {
        requireNotNull(savedStateHandle.get<Long>("userId"))
    }

    private var selectedReportReason: UiReportReason? = null

    override fun createInitialState(): ReportContract.UiState =
        ReportContract.UiState()

    override suspend fun handleEvent(event: ReportContract.UiEvent) {
        when (event) {
            is ReportContract.UiEvent.SelectReportReason -> {
                selectedReportReason = event.reportReason
            }

            is ReportContract.UiEvent.OnReport -> {
                report(event.customReason)
            }

            ReportContract.UiEvent.GetReportReasons -> {
                getReportReasons()
            }
        }
    }

    override suspend fun loadInitialData() {
        val initResult = initNavArgumentData()
        if (!initResult) return
    }

    // 초기 데이터 로드: 이전 백스택에서 넘어온 인자 값 로드
    private fun initNavArgumentData(): Boolean = runCatching {
        reportedId
    }
        .onFailure {
            viewModelScope.launch {
                showSnackBarAndCloseScreen(ReportErrorType.NotSelectedReportedId.asUiText())
            }
        }
        .isSuccess

    // 사용자 신고
    private fun report(customReason: String?) {
        selectedReportReason?.let {
            reportUseCase(
                reportedId = reportedId,
                reasonId = it.id,
                customReason = customReason,
            ).onEach { result ->
                when (result) {
                    Result.Loading -> {
                        updateState {
                            this.copy(loading = true)
                        }
                    }

                    is Result.Error -> {
                        updateState {
                            this.copy(loading = false)
                        }
                        showSnackBarAndCloseScreen(result.error.asUiText())
                    }

                    is Result.Success -> {
                        updateState {
                            this.copy(reportResult = result.data)
                        }
                        sendEffect {
                            ReportContract.UiEffect.NavigateToReportResult
                        }
                        updateState {
                            this.copy(loading = false)
                        }
                    }
                }
            }.launchIn(viewModelScope)
        } ?: run {
            // 신고 사유 미 선택 시 에러 발생
            viewModelScope.launch {
                showSnackBarAndCloseScreen(ReportErrorType.NotSelectedReportReason.asUiText())
            }
        }
    }

    private fun getReportReasons() {
        getReportReasonsUseCase().onEach { result ->
            when (result) {
                Result.Loading -> {
                    updateState {
                        this.copy(loading = true)
                    }
                }

                is Result.Error -> {
                    updateState {
                        this.copy(loading = false)
                    }
                    showSnackBarAndCloseScreen(result.error.asUiText())
                }

                is Result.Success -> {
                    updateState {
                        this.copy(
                            loading = false,
                            reportReasons = result.data.reasons.map { it.toUiModel() },
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private suspend fun showSnackBarAndCloseScreen(message: UiText) {
        sendEffect {
            ReportContract.UiEffect.CloseReportModal
        }
        snackbarController.sendEvent(SnackbarEvent(message = message))
    }
}
