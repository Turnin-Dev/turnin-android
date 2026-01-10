package com.peekr.presentation.keywordEdit.error

import com.peekr.core.presentation.common.error.asUiText
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.core.presentation.ui.util.UiText.StringResource
import com.peekr.domain.keywordEdit.error.KeywordEditErrorType
import com.peekr.presentation.R

fun KeywordEditErrorType.asUiText(): UiText = when (this) {
    KeywordEditErrorType.MyUserIdNotFound -> StringResource(R.string.keyword_edit_error_my_user_id_not_found)
    is KeywordEditErrorType.CommonError -> this.error.asUiText()
    is KeywordEditErrorType.ValidationError -> this.error.asUiText()
}
