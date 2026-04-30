package com.turnin.presentation.keywordEdit.state

import com.turnin.core.presentation.common.viewmodel.BaseUiEffect
import com.turnin.core.presentation.common.viewmodel.BaseUiEvent
import com.turnin.core.presentation.common.viewmodel.BaseUiState
import com.turnin.core.presentation.ui.util.UiText
import com.turnin.presentation.profile.state.KeywordTextFieldState

/**
 * 키워드 편집 UI 계약
 */
class KeywordEditContract {
    /**
     * UI 상태
     *
     * @property keyword 키워드 (텍스트필드 상태)
     * @property description 키워드 내용
     * @property loading 로딩
     * @property error 에러 메시지
     */
    data class UiState(
        val keyword: KeywordTextFieldState = KeywordTextFieldState(),
        val description: String = "",
        val loading: Boolean = false,
        val error: UiText? = null,
    ) : BaseUiState

    sealed interface UiEvent : BaseUiEvent {
        /** 키워드 변경 이벤트 */
        data class OnKeywordChanged(val value: String) : UiEvent

        /** 키워드 내용 변경 이벤트 */
        data class OnDescriptionChanged(val value: String) : UiEvent

        /** 키워드 추가/수정 이벤트 */
        data object AddOrUpdateKeyword : UiEvent

        /** 안전하게 뒤로가기 */
        data object SafeBackPressed : UiEvent

        /** 화면 닫기 이벤트 */
        data object CloseScreen : UiEvent
    }

    sealed interface UiEffect : BaseUiEffect {
        /** 화면 닫기 이펙트 */
        data object CloseScreen : UiEffect

        /** 취소 경고 모달 열기 */
        data object OpenSafeCancelModal : UiEffect
    }
}
