package com.peekr.presentation.keywordDetail.state

import com.peekr.core.presentation.common.viewmodel.BaseUiEffect
import com.peekr.core.presentation.common.viewmodel.BaseUiEvent
import com.peekr.core.presentation.common.viewmodel.BaseUiState
import com.peekr.core.presentation.ui.util.UiText

class KeywordDetailContract {
    /**
     * 키워드 상세정보 모달 UI 상태 클래스
     *
     * @property myKeyword 내 키워드 여부
     * @property keyword 키워드
     * @property description 키워드 설명
     * @property userName 사용자 명
     * @property profileImageUrl 사용자 프로필 사진 URL
     * @property createdAt 키워드 생성 일자
     * @property loading 로딩 여부
     * @property error 에러 상태
     */
    data class UiState(
        val myKeyword: Boolean = false,
        val keyword: String = "",
        val description: String = "",
        val userName: String = "",
        val profileImageUrl: String? = null,
        val createdAt: String = "",
        val loading: Boolean = false,
        val error: UiText? = null,
    ) : BaseUiState

    sealed interface UiEvent : BaseUiEvent

    sealed interface UiEffect : BaseUiEffect
}
