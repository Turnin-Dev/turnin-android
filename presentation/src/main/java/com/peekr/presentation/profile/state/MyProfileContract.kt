package com.peekr.presentation.profile.state

import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.presentation.common.viewmodel.BaseUiEffect
import com.peekr.core.presentation.common.viewmodel.BaseUiEvent
import com.peekr.core.presentation.common.viewmodel.BaseUiState
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.presentation.profile.model.UiMyProfile

/**
 * 나의 프로필 UI 계약
 */
class MyProfileContract {
    /**
     * 나의 프로필 상태 클래스
     *
     * @param myProfile UI용 프로필
     * @param selectedKeyword 선택된 키워드
     * @param loading 로딩 여부
     * @param fullScreenLoading 전체 화면 로딩 여부
     * @param error 에러 메시지
     */
    data class UiState(
        val myProfile: UiMyProfile? = null,
        val selectedKeyword: SelectedKeywordState = SelectedKeywordState(),
        val loading: Boolean = false,
        val fullScreenLoading: Boolean = false,
        val error: UiText? = null,
    ) : BaseUiState

    sealed interface UiEvent : BaseUiEvent {
        /** 모든 모달을 닫고 텍스트필드를 초기화하는 이벤트 */
        data object CloseAllModalsAndResetTextField : UiEvent

        /** 키워드 삭제 이벤트 */
        data class DeleteKeyword(
            val userKeywordId: UserKeywordId?,
        ) : UiEvent

        /** 선택된 키워드 변경 이벤트 */
        data class OnSelectedKeywordChanged(
            val userKeywordId: UserKeywordId,
            val keyword: String,
        ) : UiEvent

        /** 프로필 소개글 업데이트 */
        data class UpdateIntroduce(val introduce: String) : UiEvent
    }

    sealed interface UiEffect : BaseUiEffect {
        /** 취소 경고 모달 열기 */
        data object OpenSafeCancelModal : UiEffect

        /** 모든 모달 닫기 */
        data object CloseAllModals : UiEffect
    }
}
