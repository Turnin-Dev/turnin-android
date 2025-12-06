package com.peekr.presentation.report.state

import com.peekr.core.presentation.common.viewmodel.BaseUiEffect
import com.peekr.core.presentation.common.viewmodel.BaseUiEvent
import com.peekr.core.presentation.common.viewmodel.BaseUiState
import com.peekr.presentation.report.model.UiReportReason

class ReportContract {
    data class UiState(
        val reportReasons: List<UiReportReason> = emptyList(),
        val reportResult: Boolean? = null,
        val loading: Boolean = false,
    ) : BaseUiState

    sealed interface UiEvent : BaseUiEvent {
        data class SelectReportReason(
            val reportReason: UiReportReason,
        ) : UiEvent

        data class OnReport(
            val customReason: String?,
        ) : UiEvent

        data object GetReportReasons : UiEvent
    }

    sealed interface UiEffect : BaseUiEffect {
        data object NavigateToReportResult : UiEffect

        data object CloseReportModal : UiEffect
    }
}
