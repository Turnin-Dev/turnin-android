package com.peekr.presentation.profile.view.modal

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.peekr.presentation.R
import com.peekr.presentation.profile.state.KeywordTextFieldState

/**
 * 키워드 수정 모달
 *
 * @param modifier [Modifier]
 * @param isOpen 모달 표시 유무
 * @param loading 로딩 표시 여부
 * @param keywordTextFieldReadOnly 키워드 텍스트 필드 활성화
 * @param keywordTextFieldState 키워드 텍스트 필드 상태
 * @param keywordDescTextFieldState 키워드 내용 텍스트 필드 상태
 * @param onKeywordTextChanged 키워드 텍스트 변화 시 콜백
 * @param onKeywordDescTextChanged 키워드 내용 텍스트 변화 시 콜백
 * @param onEditClick 수정 클릭 시 수행할 작업
 * @param onCancelClick 취소 클릭 시 수행할 작업
 * @param onAnimationFinished 모달이 사라지고 애니메이션까지 끝나고 나서 수행할 작업
 */
@Composable
internal fun EditKeywordModal(
    modifier: Modifier = Modifier,
    isOpen: Boolean,
    loading: Boolean,
    keywordTextFieldReadOnly: Boolean = false,
    keyword: String,
    keywordDescTextFieldState: KeywordTextFieldState,
    onKeywordDescTextChanged: (String) -> Unit,
    onEditClick: () -> Unit,
    onCancelClick: () -> Unit,
    onAnimationFinished: (() -> Unit)? = null,
) {
    KeywordModal(
        modifier = modifier,
        title = stringResource(R.string.my_profile_modal_edit_keyword_btn_edit),
        acceptLabel = stringResource(R.string.my_profile_screen_edit_keyword_modal_accept),
        isOpen = isOpen,
        loading = loading,
        keywordTextFieldReadOnly = keywordTextFieldReadOnly,
        keywordTextFieldState = KeywordTextFieldState(keyword),
        keywordDescTextFieldState = keywordDescTextFieldState,
        onKeywordTextChanged = {},
        onKeywordDescTextChanged = onKeywordDescTextChanged,
        onAcceptClick = onEditClick,
        onCancelClick = onCancelClick,
        onAnimationFinished = onAnimationFinished,
    )
}
