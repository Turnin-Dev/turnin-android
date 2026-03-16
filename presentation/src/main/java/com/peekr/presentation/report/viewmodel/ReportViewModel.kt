package com.peekr.presentation.report.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.peekr.core.domain.common.Result
import com.peekr.core.presentation.common.viewmodel.MVIBaseViewModel
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

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportUseCase: ReportUseCase,
    private val getReportReasonsUseCase: GetReportReasonsUseCase,
    savedStateHandle: SavedStateHandle,
) : MVIBaseViewModel<ReportContract.UiState, ReportContract.UiEvent, ReportContract.UiEffect>() {
    private val reportedId: Long? = savedStateHandle.get<Long>("userId")

    private val reportedUserKeywordId: Long? = savedStateHandle.get<Long>("userKeywordId")

    private var selectedReportReason: UiReportReason? = null

    override fun createInitialState(): ReportContract.UiState =
        ReportContract.UiState()

    init {
        if (reportedId == null && reportedUserKeywordId == null) {
            sendEffect {
                ReportContract.UiEffect.CloseReportModal
            }
        }
    }

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

    // 신고
    private fun report(customReason: String?) {
        // 신고 사유 미 선택 시 에러 발생
        if (selectedReportReason == null) {
            sendEffect {
                ReportContract.UiEffect.CloseReportModal
            }
            return
        }

        // 신고 수행
        reportUseCase(
            reportedId = reportedId,
            reportedUserKeywordId = reportedUserKeywordId,
            reasonId = selectedReportReason!!.id,
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
                        this.copy(
                            loading = false,
                            error = result.error.asUiText(),
                        )
                    }
                    sendEffect {
                        ReportContract.UiEffect.NavigateToReportResult
                    }
                }

                is Result.Success -> {
                    updateState {
                        this.copy(loading = false)
                    }
                    sendEffect {
                        ReportContract.UiEffect.NavigateToReportResult
                    }
                }
            }
        }.launchIn(viewModelScope)
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
                        this.copy(
                            loading = false,
                            error = result.error.asUiText(),
                        )
                    }
                }

                is Result.Success -> {
                    updateState {
                        this.copy(
                            loading = false,
                            error = null,
                            reportReasons = result.data.reasons.map { it.toUiModel() },
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }
}
