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
     * @property deletionConfirmText 삭제 확인 텍스트
     * @property isDeletionEnabled 삭제 버튼 활성화 여부
     * @property fullScreenLoading 전체 화면 로딩 여부
     * @property accountInfoLoading 계정 정보 로딩 여부
     */
    data class UiState(
        val deletionConfirmText: String = "",
        val isDeletionEnabled: Boolean = false,
        val fullScreenLoading: Boolean = false,
        val accountInfoLoading: Boolean = false,
    ) : BaseUiState

    sealed interface UiEvent : BaseUiEvent {
        /** 계정 정보 화면 이동 이벤트 */
        data object OnNavigateToAccountInfo : UiEvent

        /** 차단 사용자 관리 화면 이동 이벤트 */
        data object OnNavigateToBlockList : UiEvent

        /** 버전 정보 화면 이동 이벤트 */
        data object OnNavigateToVersionInfo : UiEvent

        /** 로그아웃 클릭 이벤트 */
        data object OnLogoutClick : UiEvent

        /** 로그아웃 이벤트 */
        data object Logout : UiEvent

        /** 문의 화면 이동 이벤트 */
        data object OnNavigateToQna : UiEvent

        /** 알림 화면 이동 이벤트 */
        data object OnNavigateToNotification : UiEvent

        /** 삭제 확인 텍스트 변경 이벤트 */
        data class OnDeletionConfirmTextChanged(
            val text: String,
        ) : UiEvent

        /** 삭제 확인 상태 값 초기화 이벤트 */
        data object OnDeletionStateCleared : UiEvent

        /** 계정 삭제 클릭 이벤트 */
        data object OnDeleteAccountClick : UiEvent

        /** 계정 삭제 */
        data object DeleteAccount : UiEvent
    }

    sealed interface UiEffect : BaseUiEffect {
        /** 계정 정보 화면 이동 일회성 이벤트 */
        data class NavigateToAccountInfo(
            val accountInfo: UiAccountInfo?,
        ) : UiEffect

        /** 차단 사용자 관리 화면 이동 일회성 이벤트 */
        data object NavigateToBlockList : UiEffect

        /** 로그인 화면 이동 일회성 이벤트 */
        data object NavigateToLogin : UiEffect

        /** 버전 정보 화면 이동 일회성 이벤트 */
        data object NavigateToVersionInfo : UiEffect

        /** 알림 화면 이동 일회성 이벤트 */
        data object NavigateToNotification : UiEffect

        /** 문의 화면 이동 일회성 이벤트 */
        data class NavigateToQna(
            val qnaUrl: String,
        ) : UiEffect

        /** 로그아웃 모달 열기 일회성 이벤트 */
        data object OpenLogoutModal : UiEffect

        /** 계정 삭제 모달 열기 일회성 이벤트 */
        data object OpenDeleteAccountModal : UiEffect

        /** 계정 삭제 모달 닫기 일회성 이벤트 */
        data object CloseDeleteAccountModal : UiEffect
    }
}
