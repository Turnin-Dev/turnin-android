package com.peekr.presentation.setting.state

import com.peekr.core.presentation.common.viewmodel.BaseUiEffect
import com.peekr.core.presentation.common.viewmodel.BaseUiEvent
import com.peekr.core.presentation.common.viewmodel.BaseUiState
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.presentation.setting.model.UiAccountInfo

/**
 * 설정 UI 계약
 */
class SettingContract {
    /**
     * 설정 화면 UI 상태
     *
     * @property accountInfo 계정 정보
     * @property isAccountInfoEdited 계정 정보 수정 여부
     * @property fullScreenLoading 전체 화면 로딩 여부
     * @property error 공통 에러
     */
    data class UiState(
        val accountInfo: UiAccountInfo? = null,
        val isAccountInfoEdited: Boolean = false,
        val fullScreenLoading: Boolean = false,
        val error: UiText? = null,
    ) : BaseUiState

    sealed interface UiEvent : BaseUiEvent

    sealed interface UiEffect : BaseUiEffect
}
