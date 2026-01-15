package com.peekr.presentation.profile.state

import com.peekr.core.domain.friend.model.FriendStatus
import com.peekr.core.presentation.common.viewmodel.BaseUiEffect
import com.peekr.core.presentation.common.viewmodel.BaseUiEvent
import com.peekr.core.presentation.common.viewmodel.BaseUiState
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.presentation.profile.model.UiKeywordDetail
import com.peekr.presentation.profile.model.UiUserProfile

/**
 * 사용자 프로필 UI 계약
 */
class UserProfileContract {
    /**
     * 사용자 프로필 UI 상태
     *
     * @property profile 사용자 프로필
     * @property profileError 사용자 프로필 에러 메시지
     * @property keywords 사용자 키워드 리스트
     * @property keywordsError 사용자 키워드 리스트 에러 메시지
     * @property loading 로딩 여부
     * @property error 전체 에러 메시지
     */
    data class UiState(
        val profile: UiUserProfile? = null,
        val profileError: UiText? = null,
        val keywords: List<UiKeywordDetail> = emptyList(),
        val keywordsError: UiText? = null,
        val loading: Boolean = false,
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
