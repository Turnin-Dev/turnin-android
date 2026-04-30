package com.turnin.presentation.block.state

import com.turnin.core.presentation.common.viewmodel.BaseUiEffect
import com.turnin.core.presentation.common.viewmodel.BaseUiEvent
import com.turnin.core.presentation.common.viewmodel.BaseUiState
import com.turnin.core.presentation.ui.util.UiText
import com.turnin.presentation.block.model.UiBlockReason

/**
 * 차단 모달 UI 계약
 */
class BlockModalContract {
    /**
     * 차단 UI 상태
     *
     * @param blockReasons 차단 사유 리스트
     * @param error 공통 에러
     * @param loading 공통 로딩
     */
    data class UiState(
        val blockReasons: List<UiBlockReason> = emptyList(),
        val error: UiText? = null,
        val loading: Boolean = false,
    ) : BaseUiState

    sealed interface UiEvent : BaseUiEvent {
        /** 차단 사유 목록 조회 이벤트 */
        data object GetBlockReasons : UiEvent

        /** 차단 사유 선택 이벤트 */
        data class SelectBlockReason(
            val blockReason: UiBlockReason,
        ) : UiEvent

        /** 차단 수행 이벤트 */
        data class OnBlock(
            val reason: String,
        ) : UiEvent
    }

    sealed interface UiEffect : BaseUiEffect {
        /** 차단 모달 닫기 일회성 이벤트 */
        data object CloseBlockModal : UiEffect

        /** 차단 결과 이동 일회성 이벤트 */
        data object NavigateToBlockResult : UiEffect
    }
}
