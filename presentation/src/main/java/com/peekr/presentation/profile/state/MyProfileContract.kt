package com.peekr.presentation.profile.state

import com.peekr.core.presentation.common.viewmodel.BaseUiEffect
import com.peekr.core.presentation.common.viewmodel.BaseUiEvent
import com.peekr.core.presentation.common.viewmodel.BaseUiState
import com.peekr.core.presentation.ui.model.UiUserKeyword
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.presentation.profile.model.UiMyProfile

/**
 * 나의 프로필 UI 계약
 */
class MyProfileContract {
    /**
     * 나의 프로필 상태 클래스
     *
     * @param myProfile 나의 프로필
     * @param myProfileLoading 나의 프로필 로딩
     * @param myKeywords 나의 키워드
     * @param myKeywordsLoading 나의 키워드 로딩
     * @param error 공통 에러 메시지
     */
    data class UiState(
        val myProfile: UiMyProfile? = null,
        val myProfileLoading: Boolean = false,
        val myKeywords: List<UiUserKeyword>? = null,
        val myKeywordsLoading: Boolean = false,
        val error: UiText? = null,
    ) : BaseUiState

    sealed interface UiEvent : BaseUiEvent {
        /** 프로필 새로고침 이벤트 */
        data object Refresh : UiEvent
    }

    sealed interface UiEffect : BaseUiEffect
}
