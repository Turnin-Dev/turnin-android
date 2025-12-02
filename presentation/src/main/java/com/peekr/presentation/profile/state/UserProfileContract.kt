package com.peekr.presentation.profile.state

import com.peekr.core.presentation.common.viewmodel.BaseUiEffect
import com.peekr.core.presentation.common.viewmodel.BaseUiEvent
import com.peekr.core.presentation.common.viewmodel.BaseUiState
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.presentation.profile.model.UiUserProfile

/**
 * 사용자 프로필 UI 계약
 */
class UserProfileContract {
    data class UiState(
        val userProfile: UiUserProfile? = null,
        val loading: Boolean = false,
        val error: UiText? = null,
    ) : BaseUiState

    sealed interface UiEvent : BaseUiEvent

    sealed interface UiEffect : BaseUiEffect
}
