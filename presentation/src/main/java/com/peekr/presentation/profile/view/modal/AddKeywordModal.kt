package com.peekr.presentation.profile.view.modal

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.peekr.presentation.R
import com.peekr.presentation.profile.state.KeywordTextFieldState

/**
 * 키워드 추가 모달
 *
 * @param modifier [Modifier]
 * @param isOpen 모달 표시 유무
 * @param loading 로딩 표시 여부
 * @param keywordTextFieldState 키워드 텍스트 필드 상태
 * @param keywordDescTextFieldState 키워드 내용 텍스트 필드 상태
 * @param onKeywordTextChanged 키워드 텍스트 변화 시 콜백
 * @param onKeywordDescTextChanged 키워드 내용 텍스트 변화 시 콜백
 * @param onAddClick 추가 클릭 시 수행할 작업
 * @param onCancelClick 취소 클릭 시 수행할 작업
 * @param onAnimationFinished 모달이 사라지고 애니메이션까지 끝나고 나서 수행할 작업
 */
@Composable
internal fun AddKeywordModal(
    modifier: Modifier = Modifier,
    isOpen: Boolean,
    loading: Boolean,
    keywordTextFieldState: KeywordTextFieldState,
    keywordDescTextFieldState: KeywordTextFieldState,
    onKeywordTextChanged: (String) -> Unit,
    onKeywordDescTextChanged: (String) -> Unit,
    onAddClick: () -> Unit,
    onCancelClick: () -> Unit,
    onAnimationFinished: (() -> Unit)? = null,
) {
    KeywordModal(
        modifier = modifier,
        title = stringResource(R.string.my_profile_modal_add_keyword_title),
        acceptLabel = stringResource(R.string.my_profile_modal_add_keyword_btn_accept),
        isOpen = isOpen,
        loading = loading,
        keywordTextFieldState = keywordTextFieldState,
        keywordDescTextFieldState = keywordDescTextFieldState,
        onKeywordTextChanged = onKeywordTextChanged,
        onKeywordDescTextChanged = onKeywordDescTextChanged,
        onAcceptClick = onAddClick,
        onCancelClick = onCancelClick,
        onAnimationFinished = onAnimationFinished,
    )
}
