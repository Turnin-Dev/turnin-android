package com.peekr.presentation.keywordDetail.state

import androidx.compose.ui.text.input.TextFieldValue
import com.peekr.core.presentation.util.UiText
import com.peekr.core.presentation.viewmodel.BaseUiEffect
import com.peekr.core.presentation.viewmodel.BaseUiEvent
import com.peekr.core.presentation.viewmodel.BaseUiState

class KeywordDetailContract {
    /**
     * 키워드 상세정보 모달 UI 상태 클래스
     *
     * @property keyword 키워드
     * @property description 키워드 설명
     * @property myKeyword 내 키워드 여부
     * @property editMode 수정 모드 활성화 여부
     * @property loading 로딩 상태
     * @property error 에러 상태
     */
    data class UiState(
        val keyword: String = "",
        val description: TextFieldValue = TextFieldValue(""),
        val myKeyword: Boolean = false,
        val editMode: Boolean = false,
        val loading: Boolean = false,
        val loadingDescription: Boolean = false,
        val error: UiText? = null,
    ) : BaseUiState

    sealed interface UiEvent : BaseUiEvent {
        /** UI 상에서 키워드 설명 수정 시 콜백 이벤트 */
        data class OnDescriptionChanged(
            val value: TextFieldValue,
        ) : UiEvent

        /** 키워드 설명 수정 완료 시 콜백 이벤트 */
        data class UpdateDescription(
            val description: String,
        ) : UiEvent

        /** 안전 취소 이벤트 */
        data object SafeCancel : UiEvent

        /** 수정 모드 활성화 */
        data object EnableEditMode : UiEvent
    }

    sealed interface UiEffect : BaseUiEffect {
        /** 치명적인 에러 발생으로 전체화면에 에러를 표시해야한다. */
        data class FullScreenError(
            val errorMessage: UiText = UiText.DynamicString(""),
        ) : UiEffect

        /** 뒤로가기 */
        data object BackStack : UiEffect
    }
}
