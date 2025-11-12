package com.peekr.presentation.profile.state

import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.presentation.util.UiText
import com.peekr.core.presentation.viewmodel.BaseUiEffect
import com.peekr.core.presentation.viewmodel.BaseUiEvent
import com.peekr.core.presentation.viewmodel.BaseUiState
import com.peekr.presentation.profile.model.UiProfile

class ProfileContract {
    /**
     * 프로필 상태 클래스
     *
     * @param profile UI용 프로필
     * @param keywordTextField 키워드 텍스트 필드 상태
     * @param keywordDescTextField 키워드 내용 텍스트 필드 상태
     * @param loading 로딩 여부
     * @param error 에러 메시지
     */
    data class UiState(
        val profile: UiProfile? = null,
        val keywordTextField: KeywordTextFieldState = KeywordTextFieldState(),
        val keywordDescTextField: KeywordTextFieldState = KeywordTextFieldState(),
        val updatedKeywordNodesOffset: Map<UserKeywordId, ChangedKeywordNodeOffset> =
            emptyMap<UserKeywordId, ChangedKeywordNodeOffset>(),
        val loading: Boolean = false,
        val error: UiText? = null,
    ) : BaseUiState

    sealed interface UiEvent : BaseUiEvent {
        /** 키워드 추가 모달에서 키워드 텍스트 필드 값 변경 이벤트 */
        data class OnKeywordTextChanged(val value: String) : UiEvent

        /** 키워드 추가 모달에서 키워드 설명 텍스트 필드 값 변경 이벤트 */
        data class OnKeywordDescTextChanged(val value: String) : UiEvent

        /** 키워드 위치 변경 이벤트 */
        data class OnKeywordNodeOffsetChanged(
            val userKeywordId: UserKeywordId,
            val offsetX: Float,
            val offsetY: Float,
        ) : UiEvent

        /** 키워드 위치 변경 업데이트 이벤트 */
        data object UpdateKeywordNodeOffset : UiEvent

        /** 키워드 위치 기존 값으로 초기화 이벤트 */
        data object ResetKeywordNodeOffset : UiEvent

        /** 키워드 추가 이벤트 */
        data class AddKeyword(
            val keyword: String,
            val description: String,
        ) : UiEvent

        /** 키워드 삭제 이벤트 */
        data class DeleteKeyword(
            val userKeywordId: UserKeywordId?,
        ) : UiEvent

        /** 키워드 추가 모달에서 취소 전에 텍스트 필드에 입력된 값이 있는지 확인하는 이벤트 */
        data class CheckSafeCancel(
            val keyword: String?,
            val description: String?,
        ) : UiEvent

        /** 모든 모달을 닫기 이벤트 */
        data object AcceptSafeCancel : UiEvent
    }

    sealed interface UiEffect : BaseUiEffect {
        /** 취소 경고 모달 열기 */
        data object OpenSafeCancelModal : UiEffect

        /** 모든 모달 닫기 */
        data object CloseAllModal : UiEffect

        /** 선택된 데이터(키워드, 설명, ID 등) */
        data object ResetSelectedData : UiEffect
    }
}
