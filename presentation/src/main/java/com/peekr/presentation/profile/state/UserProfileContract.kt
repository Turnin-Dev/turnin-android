package com.peekr.presentation.profile.state

import com.peekr.core.domain.friend.model.FriendStatus
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

    sealed interface UiEvent : BaseUiEvent {
        /**
         * 신고 이벤트
         */
        data object OnReport : UiEvent

        /**
         * 친구 버튼 클릭 이벤트
         */
        data class OnFriendButtonClick(
            val friendStatus: FriendStatus,
        ) : UiEvent

        /**
         * 친구 삭제 이벤트
         */
        data object DeleteFriend : UiEvent
    }

    sealed interface UiEffect : BaseUiEffect {
        data class NavigateToReport(
            val userId: Long,
        ) : UiEffect

        data object OpenDeleteFriendModal : UiEffect
    }
}
