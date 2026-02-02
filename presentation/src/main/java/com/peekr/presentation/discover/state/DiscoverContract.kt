package com.peekr.presentation.discover.state

import com.peekr.core.presentation.common.viewmodel.BaseUiEffect
import com.peekr.core.presentation.common.viewmodel.BaseUiEvent
import com.peekr.core.presentation.common.viewmodel.BaseUiState
import com.peekr.presentation.discover.model.UiHistoryUser

/**
 * 탐색 화면 UI 계약
 */
class DiscoverContract {
    /**
     * UI 상태
     *
     * @property currentTargetUserId 현재 탐색 대상 사용자 ID
     */
    data class UiState(
        val currentTargetUserId: Long? = null,
        val historyUsers: List<UiHistoryUser> = emptyList(),
    ) : BaseUiState

    sealed interface UiEvent : BaseUiEvent {
        data class OnSelectedHistoryUser(
            val historyUser: UiHistoryUser,
        ) : UiEvent
    }

    sealed interface UiEffect : BaseUiEffect
}
