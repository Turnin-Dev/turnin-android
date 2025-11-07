package com.peekr.presentation.keywordDetail.error

import com.peekr.core.presentation.error.asUiText
import com.peekr.core.presentation.util.UiText
import com.peekr.domain.keywordDetail.error.KeywordDetailErrorType
import com.peekr.presentation.R

internal fun KeywordDetailErrorType.asUiText(): UiText = when (this) {
    is KeywordDetailErrorType.Unexpected -> UiText.StringResource(R.string.keyword_detail_modal_error_unexpected)
    is KeywordDetailErrorType.UserError -> this.error.asUiText()
}
