package com.peekr.core.presentation.error

import com.peekr.core.domain.file.FileErrorType
import com.peekr.core.presentation.R
import com.peekr.core.presentation.util.UiText

fun FileErrorType.asUiText(): UiText = when (this) {
    is FileErrorType.CommonError -> this.error.asUiText()
    is FileErrorType.Unexpected -> UiText.StringResource(R.string.file_error_unexpected)
}
