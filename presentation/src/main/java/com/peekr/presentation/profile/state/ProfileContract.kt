package com.peekr.presentation.profile.state

import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.presentation.util.UiText
import com.peekr.core.presentation.viewmodel.BaseUiEffect
import com.peekr.core.presentation.viewmodel.BaseUiEvent
import com.peekr.core.presentation.viewmodel.BaseUiState
import com.peekr.presentation.profile.model.UiProfile

class ProfileContract {
    /**
     * 프로필 상태 클래스
     *
     * @param profile UI용 프로필
     * @param keywordTextField 키워드 텍스트 필드 상태
     * @param keywordDescTextField 키워드 내용 텍스트 필드 상태
     * @param loading 로딩 여부
     * @param error 에러 메시지
     */
    data class UiState(
        val profile: UiProfile? = null,
        val keywordTextField: KeywordTextFieldState = KeywordTextFieldState(),
        val keywordDescTextField: KeywordTextFieldState = KeywordTextFieldState(),
        val updatedKeywordNodesOffset: Map<UserKeywordId, ChangedKeywordNodeOffset> =
            emptyMap<UserKeywordId, ChangedKeywordNodeOffset>(),
        val loading: Boolean = false,
        val error: UiText? = null,
    ) : BaseUiState

    sealed interface UiEvent : BaseUiEvent {
        data class OnKeywordTextChanged(val value: String) : UiEvent

        data class OnKeywordDescTextChanged(val value: String) : UiEvent

        data class OnKeywordNodeOffsetChanged(
            val userKeywordId: UserKeywordId,
            val offsetX: Float,
            val offsetY: Float,
        ) : UiEvent

        data object UpdateKeywordNodeOffset : UiEvent

        data object ResetKeywordNodeOffset : UiEvent

        data object AddKeyword : UiEvent
    }

    sealed interface UiEffect : BaseUiEffect
}
