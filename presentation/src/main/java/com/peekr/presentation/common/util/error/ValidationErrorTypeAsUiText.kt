package com.peekr.presentation.common.util.error

import com.peekr.domain.common.util.ValidationError
import com.peekr.presentation.R
import com.peekr.presentation.common.util.UiText

fun ValidationError.asUiText(): UiText = when (this) {
    // ------------------------------ DisplayID ------------------------------
    ValidationError.DisplayId.Empty -> {
        UiText.StringResource(R.string.validation_display_id_empty)
    }

    is ValidationError.DisplayId.TooShortOrLong -> {
        UiText.DynamicString("${this.min}~${this.max}자 이내만 가능합니다.")
    }

    is ValidationError.DisplayId.InvalidFormat -> {
        UiText.DynamicString("${this.format}만 가능합니다.")
    }
    // ------------------------------ Introduce ------------------------------
    is ValidationError.Introduce.TooLong -> {
        UiText.DynamicString("${this.max}자 이내만 가능합니다.")
    }
    // ------------------------------ Name ------------------------------
    ValidationError.Name.Empty -> {
        UiText.StringResource(R.string.validation_name_empty)
    }

    is ValidationError.Name.TooShortOrLong -> {
        UiText.DynamicString("${this.min}~${this.max}자 이내만 가능합니다.")
    }

    is ValidationError.Name.InvalidFormat -> {
        UiText.DynamicString("${this.format}만 가능합니다.")
    }
}
