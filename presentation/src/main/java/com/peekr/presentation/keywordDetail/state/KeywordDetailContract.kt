package com.peekr.presentation.keywordDetail.state

import com.peekr.core.presentation.common.viewmodel.BaseUiEffect
import com.peekr.core.presentation.common.viewmodel.BaseUiEvent
import com.peekr.core.presentation.common.viewmodel.BaseUiState
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.presentation.keywordDetail.model.UiKeywordDetail

class KeywordDetailContract {
    /**
     * 키워드 상세정보 모달 UI 상태 클래스
     *
     * @property myKeyword 내 키워드 여부
     * @property keywordDetail 키워드 상세 정보
     * @property loading 로딩 여부
     * @property error 에러 상태
     */
    data class UiState(
        val myKeyword: Boolean = false,
        val keywordDetail: UiKeywordDetail? = null,
        val loading: Boolean = false,
        val error: UiText? = null,
    ) : BaseUiState

    sealed interface UiEvent : BaseUiEvent {
        /** 키워드 상세정보 새로고침 이벤트 */
        data object OnRefresh : UiEvent

        /** 키워드 신고 이벤트 */
        data object OnReport : UiEvent
    }

    sealed interface UiEffect : BaseUiEffect {
        /** 신고 화면 이동 이펙트 */
        data class NavigateToReport(
            val userId: Long?,
            val userKeywordId: Long?,
        ) : UiEffect
    }
}
