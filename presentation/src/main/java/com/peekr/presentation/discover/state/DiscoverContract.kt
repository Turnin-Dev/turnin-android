package com.peekr.presentation.discover.state

import com.peekr.core.presentation.common.viewmodel.BaseUiEffect
import com.peekr.core.presentation.common.viewmodel.BaseUiEvent
import com.peekr.core.presentation.common.viewmodel.BaseUiState
import com.peekr.presentation.discover.model.UiDiscoverContext

/**
 * 탐색 화면 UI 계약
 */
class DiscoverContract {
    /**
     * UI 상태
     *
     * @property currentDiscoverTarget 현재 탐색 대상 (탐색 컨텍스트)
     * @property histories 히스토리 리스트 (탐색 컨텍스트 리스트)
     */
    data class UiState(
        val currentDiscoverTarget: UiDiscoverContext? = null,
        val histories: List<UiDiscoverContext> = emptyList(),
    ) : BaseUiState

    sealed interface UiEvent : BaseUiEvent {
        data class ChangeCurrentDiscoverTarget(
            val target: UiDiscoverContext,
        ) : UiEvent
    }

    sealed interface UiEffect : BaseUiEffect
}
