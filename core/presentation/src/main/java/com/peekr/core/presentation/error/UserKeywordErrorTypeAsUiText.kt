package com.peekr.core.presentation.error

import com.peekr.core.domain.userKeyword.error.UserKeywordErrorType
import com.peekr.core.presentation.R
import com.peekr.core.presentation.util.UiText

fun UserKeywordErrorType.asUiText(): UiText = when (this) {
    is UserKeywordErrorType.CommonError -> this.error.asUiText()
    is UserKeywordErrorType.Unexpected -> UiText.StringResource(R.string.user_keyword_error_unexpected)
}
