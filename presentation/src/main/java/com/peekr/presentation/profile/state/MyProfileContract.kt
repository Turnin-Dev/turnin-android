package com.peekr.presentation.profile.state

import com.peekr.core.presentation.common.viewmodel.BaseUiEffect
import com.peekr.core.presentation.common.viewmodel.BaseUiEvent
import com.peekr.core.presentation.common.viewmodel.BaseUiState
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.presentation.profile.model.UiKeywordDetail
import com.peekr.presentation.profile.model.UiMyProfile

/**
 * 나의 프로필 UI 계약
 */
class MyProfileContract {
    /**
     * 나의 프로필 상태 클래스
     *
     * @param myProfile UI용 나의 프로필
     * @param myProfileError 나의 프로필 에러 메시지
     * @param myKeywords UI용 나의 키워드
     * @param myKeywordsError 나의 키워드 에러 메시지
     * @param loading 부분 로딩 여부
     * @param fullScreenLoading 전체 화면 로딩 여부
     * @param error 에러 메시지
     */
    data class UiState(
        val myProfile: UiMyProfile? = null,
        val myProfileError: UiText? = null,
        val myKeywords: List<UiKeywordDetail> = emptyList(),
        val myKeywordsError: UiText? = null,
        val loading: Boolean = false,
        val fullScreenLoading: Boolean = false,
        val error: UiText? = null,
    ) : BaseUiState

    sealed interface UiEvent : BaseUiEvent

    sealed interface UiEffect : BaseUiEffect
}
