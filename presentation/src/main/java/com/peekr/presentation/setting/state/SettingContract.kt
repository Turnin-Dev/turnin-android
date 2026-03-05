package com.peekr.presentation.setting.state

import com.peekr.core.presentation.common.viewmodel.BaseUiEffect
import com.peekr.core.presentation.common.viewmodel.BaseUiEvent
import com.peekr.core.presentation.common.viewmodel.BaseUiState
import com.peekr.core.presentation.ui.util.UiText

/**
 * 설정 UI 계약
 */
class SettingContract {
    /**
     * 설정 화면 UI 상태
     *
     * @property accountInfoState 계정 정보 UI 상태
     * @property fullScreenLoading 전체 화면 로딩 여부
     * @property error 공통 에러
     */
    data class UiState(
        val accountInfoState: AccountInfoState = AccountInfoState(),
        val fullScreenLoading: Boolean = false,
        val error: UiText? = null,
    ) : BaseUiState

    sealed interface UiEvent : BaseUiEvent {
        /** 계정 정보 저장 이벤트  */
        data object OnSaveAccountInfo : UiEvent

        /**
         * 프로필 사진 변경 이벤트
         *
         * @property imageBytes 변경할 프로필 사진의 [ByteArray]
         */
        class OnProfileImageUpdated(
            val imageBytes: ByteArray,
        ) : UiEvent

        /**
         * 프로필 사진 삭제 이벤트
         */
        data object OnProfileImageDeleted : UiEvent

        data object OnAccountInfoStateCleared : UiEvent
    }

    sealed interface UiEffect : BaseUiEffect
}
