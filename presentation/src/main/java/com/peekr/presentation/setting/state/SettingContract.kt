package com.peekr.presentation.setting.state

import com.peekr.core.presentation.common.viewmodel.BaseUiEffect
import com.peekr.core.presentation.common.viewmodel.BaseUiEvent
import com.peekr.core.presentation.common.viewmodel.BaseUiState
import com.peekr.presentation.setting.model.UiAccountInfo

/**
 * 설정 UI 계약
 */
class SettingContract {
    /**
     * 설정 화면 UI 상태
     *
     * @property fullScreenLoading 전체 화면 로딩 여부
     * @property accountInfoLoading 계정 정보 로딩 여부
     */
    data class UiState(
        val fullScreenLoading: Boolean = false,
        val accountInfoLoading: Boolean = false,
    ) : BaseUiState

    sealed interface UiEvent : BaseUiEvent {
        /** 계정 정보 화면 이동 이벤트 */
        data object OnNavigateToAccountInfo : UiEvent

        /** 차단 사용자 관리 화면 이동 이벤트 */
        data object OnNavigateToBlockList : UiEvent
    }

    sealed interface UiEffect : BaseUiEffect {
        /** 계정 정보 화면 이동 일회성 이벤트 */
        data class NavigateToAccountInfo(
            val accountInfo: UiAccountInfo?,
        ) : UiEffect

        /** 차단 사용자 관리 화면 이동 일회성 이벤트 */
        data object NavigateToBlockList : UiEffect
    }
}
