package com.turnin.presentation.profile.state

import com.turnin.core.domain.friend.model.FriendStatus
import com.turnin.core.presentation.common.viewmodel.BaseUiEffect
import com.turnin.core.presentation.common.viewmodel.BaseUiEvent
import com.turnin.core.presentation.common.viewmodel.BaseUiState
import com.turnin.core.presentation.ui.model.UiUserKeyword
import com.turnin.core.presentation.ui.util.UiText
import com.turnin.presentation.profile.model.UiUserProfile

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
     * @property isRefreshing 새로고침 여부
     * @property error 공통 에러 메시지
     * @property unblockLoading 차단 해제 로딩
     */
    data class UiState(
        val previewName: String = "",
        val previewDisplayId: String = "",
        val previewProfileImageUrl: String? = null,
        val profile: UiUserProfile? = null,
        val profileLoading: Boolean = false,
        val keywords: List<UiUserKeyword>? = null,
        val keywordsLoading: Boolean = false,
        val isRefreshing: Boolean = false,
        val error: UiText? = null,
        val unblockLoading: Boolean = false,
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

        /**
         * (프로필, 키워드 리스트) 새로고침 이벤트
         */
        data object Refresh : UiEvent

        /**
         * 차단 해제 이벤트
         */
        data object Unblock : UiEvent
    }

    sealed interface UiEffect : BaseUiEffect {
        /**
         * 신고 모달 이동 일회성 이벤트
         */
        data class NavigateToReport(
            val userId: Long,
        ) : UiEffect

        /**
         * 친구 삭제 모달 열기 일회성 이벤트
         */
        data object OpenDeleteFriendModal : UiEffect
    }
}
