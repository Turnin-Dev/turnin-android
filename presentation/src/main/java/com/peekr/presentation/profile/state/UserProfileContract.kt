package com.peekr.presentation.profile.state

import com.peekr.core.domain.friend.model.FriendStatus
import com.peekr.core.presentation.common.viewmodel.BaseUiEffect
import com.peekr.core.presentation.common.viewmodel.BaseUiEvent
import com.peekr.core.presentation.common.viewmodel.BaseUiState
import com.peekr.core.presentation.ui.model.UiUserKeyword
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.presentation.profile.model.UiUserProfile

/**
 * 사용자 프로필 UI 계약
 */
class UserProfileContract {
    /**
     * 사용자 프로필 UI 상태
     *
     * @property profile 사용자 프로필
     * @property profileLoading 사용자 로딩
     * @property keywords 사용자 키워드 리스트
     * @property keywordsLoading 사용자 키워드 리스트 로딩
     * @property error 공통 에러 메시지
     */
    data class UiState(
        val profile: UiUserProfile? = null,
        val profileLoading: Boolean = false,
        val keywords: List<UiUserKeyword> = emptyList(),
        val keywordsLoading: Boolean = false,
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
