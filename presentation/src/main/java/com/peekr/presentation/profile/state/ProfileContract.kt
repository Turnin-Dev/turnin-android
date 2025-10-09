package com.peekr.presentation.profile.state

import com.peekr.core.presentation.util.BaseUiEffect
import com.peekr.core.presentation.util.BaseUiEvent
import com.peekr.core.presentation.util.BaseUiState
import com.peekr.core.presentation.util.UiText
import com.peekr.presentation.profile.model.UiProfile

class ProfileContract {
    /**
     * 프로필 상태 클래스
     *
     * @param profile UI용 프로필
     * @param loading 로딩 여부
     * @param error 에러 메시지
     */
    data class UiState(
        val profile: UiProfile? = null,
        val loading: Boolean = false,
        val error: UiText? = null,
    ) : BaseUiState

    sealed interface UiEvent : BaseUiEvent

    sealed interface UiEffect : BaseUiEffect
}
