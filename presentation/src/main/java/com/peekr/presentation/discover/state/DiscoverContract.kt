package com.peekr.presentation.discover.state

import com.peekr.core.presentation.common.viewmodel.BaseUiEffect
import com.peekr.core.presentation.common.viewmodel.BaseUiEvent
import com.peekr.core.presentation.common.viewmodel.BaseUiState
import com.peekr.presentation.discover.model.UiDiscoverContext
import com.peekr.presentation.discover.model.UiHistoryUser

/**
 * 탐색 화면 UI 계약
 */
class DiscoverContract {
    /**
     * UI 상태
     *
     * @property currentTargetUser 현재 탐색 대상 사용자 (탐색 컨텍스트)
     */
    data class UiState(
        val currentTargetUser: UiDiscoverContext? = null,
        val historyUsers: List<UiHistoryUser> = emptyList(),
    ) : BaseUiState

    sealed interface UiEvent : BaseUiEvent {
        data class RefreshDiscoverContexts(
            val userId: Long,
        ) : UiEvent
    }

    sealed interface UiEffect : BaseUiEffect
}
