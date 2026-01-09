package com.peekr.presentation.keywordAdd.state

import com.peekr.core.presentation.common.viewmodel.BaseUiEffect
import com.peekr.core.presentation.common.viewmodel.BaseUiEvent
import com.peekr.core.presentation.common.viewmodel.BaseUiState
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.presentation.profile.state.KeywordTextFieldState

/**
 * 키워드 추가 UI 계약
 */
class KeywordAddContract {
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
    }

    sealed interface UiEffect : BaseUiEffect
}
