package com.turnin.presentation.report.state

import com.turnin.core.presentation.common.viewmodel.BaseUiEffect
import com.turnin.core.presentation.common.viewmodel.BaseUiEvent
import com.turnin.core.presentation.common.viewmodel.BaseUiState
import com.turnin.core.presentation.ui.util.UiText
import com.turnin.presentation.report.model.UiReportReason

/**
 * 신고 UI 계약
 */
class ReportContract {
    /**
     * 신고 UI 상태
     *
     * @param reportReasons 신고 사유 리스트
     * @param error 공통 에러
     * @param loading 공통 로딩
     */
    data class UiState(
        val reportReasons: List<UiReportReason> = emptyList(),
        val error: UiText? = null,
        val loading: Boolean = false,
    ) : BaseUiState

    sealed interface UiEvent : BaseUiEvent {
        /**
         * 신고 사유 선택 이벤트
         */
        data class SelectReportReason(
            val reportReason: UiReportReason,
        ) : UiEvent

        /**
         * 신고 수행 이벤트
         */
        data class OnReport(
            val customReason: String?,
        ) : UiEvent

        /**
         * 신고 사유 조회 이벤트
         */
        data object GetReportReasons : UiEvent
    }

    sealed interface UiEffect : BaseUiEffect {
        /**
         * 신고 결과 화면(신고 화면의 마지막 화면)으로 이동히는 이펙트
         */
        data object NavigateToReportResult : UiEffect

        /**
         * 모든 신고 모달을 닫는 이펙트
         */
        data object CloseReportModal : UiEffect
    }
}
