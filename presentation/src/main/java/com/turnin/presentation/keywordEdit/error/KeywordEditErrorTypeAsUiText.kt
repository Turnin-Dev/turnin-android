package com.turnin.presentation.keywordEdit.error

import com.turnin.core.presentation.common.error.asUiText
import com.turnin.core.presentation.ui.util.UiText
import com.turnin.core.presentation.ui.util.UiText.StringResource
import com.turnin.domain.keywordEdit.error.KeywordEditErrorType
import com.turnin.presentation.R

fun KeywordEditErrorType.asUiText(): UiText = when (this) {
    KeywordEditErrorType.MyUserIdNotFound -> StringResource(R.string.keyword_edit_error_my_user_id_not_found)
    is KeywordEditErrorType.Unexpected -> StringResource(R.string.keyword_edit_error_unexpected)
    KeywordEditErrorType.UpdateFailed -> StringResource(R.string.keyword_edit_error_update_failed)
    is KeywordEditErrorType.CommonError -> this.error.asUiText()
    is KeywordEditErrorType.ValidationError -> this.error.asUiText()
}
