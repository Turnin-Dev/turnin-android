package com.turnin.presentation.keywordDetail.error

import com.turnin.core.presentation.common.error.asUiText
import com.turnin.core.presentation.ui.util.UiText
import com.turnin.core.presentation.ui.util.UiText.StringResource
import com.turnin.domain.keywordDetail.error.KeywordDetailErrorType
import com.turnin.presentation.R

internal fun KeywordDetailErrorType.asUiText(): UiText = when (this) {
    is KeywordDetailErrorType.Unexpected -> StringResource(R.string.keyword_detail_error_unexpected)
    is KeywordDetailErrorType.CommonError -> this.error.asUiText()
    KeywordDetailErrorType.UserIdNotFound -> StringResource(R.string.keyword_detail_error_user_id_not_found)
}
