package com.turnin.core.presentation.common.error

import com.turnin.core.domain.common.validation.ValidationErrorType
import com.turnin.core.presentation.R
import com.turnin.core.presentation.ui.util.UiText
import com.turnin.core.presentation.ui.util.UiText.StringResource

fun ValidationErrorType.asUiText(): UiText = when (this) {
    is ValidationErrorType.Common.Empty -> StringResource(
        R.string.validation_error_common_empty_field,
        this.field,
    )

    is ValidationErrorType.Common.TooShortOrLong -> StringResource(
        R.string.validation_error_common_length_range,
        this.field,
        this.min,
        this.max,
    )

    is ValidationErrorType.Common.InvalidFormat -> StringResource(
        R.string.validation_error_common_invalid_format,
        this.field,
        this.format,
    )

    ValidationErrorType.Unexpected -> StringResource(
        R.string.validation_error_unexpected,
    )
}
