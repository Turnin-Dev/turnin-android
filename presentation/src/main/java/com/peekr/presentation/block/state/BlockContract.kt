package com.peekr.presentation.block.state

import com.peekr.core.presentation.common.viewmodel.BaseUiEffect
import com.peekr.core.presentation.common.viewmodel.BaseUiEvent
import com.peekr.core.presentation.common.viewmodel.BaseUiState
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.presentation.block.model.UiBlockReason

/**
 * 차단 UI 계약
 */
class BlockContract {
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

    sealed interface UiEvent : BaseUiEvent

    sealed interface UiEffect : BaseUiEffect {
        /** 차단 모달 닫기 일회성 이벤트 */
        data object CloseBlockModal : UiEffect
    }
}
