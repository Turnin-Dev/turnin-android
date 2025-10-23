package com.peekr.core.presentation.error

import com.peekr.core.domain.validation.ValidationErrorType
import com.peekr.core.presentation.R
import com.peekr.core.presentation.util.UiText

fun ValidationErrorType.asUiText(): UiText = when (this) {
    is ValidationErrorType.Common.Empty -> UiText.StringResource(
        R.string.common_validation_error_empty_field,
        this.field,
    )

    is ValidationErrorType.Common.TooShortOrLong -> UiText.StringResource(
        R.string.common_validation_error_length_range,
        this.field,
        this.min,
        this.max,
    )

    is ValidationErrorType.Common.InvalidFormat -> UiText.StringResource(
        R.string.common_validation_error_invalid_format,
        this.field,
        this.format,
    )
}
