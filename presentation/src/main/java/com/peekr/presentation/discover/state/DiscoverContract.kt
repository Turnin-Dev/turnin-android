package com.peekr.presentation.discover.state

import com.peekr.core.presentation.common.viewmodel.BaseUiEffect
import com.peekr.core.presentation.common.viewmodel.BaseUiEvent
import com.peekr.core.presentation.common.viewmodel.BaseUiState
import com.peekr.presentation.discover.model.UiDiscoverContext

/**
 * 탐색 화면 UI 계약
 */
class DiscoverContract {
    /**
     * UI 상태
     *
     * @property currentDiscoverTarget 현재 탐색 대상 (탐색 컨텍스트)
     * @property histories 히스토리 리스트 (탐색 컨텍스트 리스트)
     * @property selectedDiscoverTarget 선택된 탐색 대상 (탐색 컨텍스트)
     */
    data class UiState(
        val currentDiscoverTarget: UiDiscoverContext? = null,
        val histories: List<UiDiscoverContext> = emptyList(),
        val selectedDiscoverTarget: UiDiscoverContext? = null,
    ) : BaseUiState

    sealed interface UiEvent : BaseUiEvent {
        /**
         * 현재 탐색 대상 변경 이벤트
         */
        data class ChangeCurrentDiscoverTarget(
            val target: UiDiscoverContext,
        ) : UiEvent

        /**
         * 피드 선택 이벤트
         */
        data class SelectFeed(
            val discoverContext: UiDiscoverContext,
        ) : UiEvent

        /**
         * 재탐색 이벤트
         */
        data object ReDiscover : UiEvent

        /**
         * 키워드 상세 화면 이동 이벤트
         */
        data class NavigateToKeywordDetail(
            val userId: Long,
            val userKeywordId: Long,
        ) : UiEvent

        /**
         * 사용자 프로필 이동 이벤트
         */
        data class NavigateToUserProfile(
            val userId: Long,
        ) : UiEvent
    }

    sealed interface UiEffect : BaseUiEffect {
        /**
         * 키워드 상세 화면 이동 일회성 이벤트
         */
        data class NavigateToKeywordDetail(
            val userId: Long,
            val userKeywordId: Long,
        ) : UiEffect

        /**
         * 나의 프로필 이동 일회성 이벤트
         */
        data object NavigateToMyProfile : UiEffect

        /**
         * 사용자 프로필 이동 일회성 이벤트
         */
        data class NavigateToUserProfile(
            val userId: Long,
        ) : UiEffect
    }
}
