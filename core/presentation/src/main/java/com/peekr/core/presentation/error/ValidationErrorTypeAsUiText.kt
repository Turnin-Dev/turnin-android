package com.peekr.core.presentation.error

import com.peekr.core.domain.validation.CommonValidationError
import com.peekr.core.domain.validation.ValidationError
import com.peekr.core.presentation.util.UiText

fun ValidationError.asUiText(): UiText = when (this) {
    is CommonValidationError.Empty -> UiText.DynamicString(
        "${this.field}는 비어있을 수 없습니다.",
    )

    is CommonValidationError.TooShortOrLong -> UiText.DynamicString(
        "${this.field}는 $min ~ ${max}자 이내만 가능합니다.",
    )

    is CommonValidationError.InvalidFormat -> UiText.DynamicString(
        "${this.field}는 ${format}만 가능합니다.",
    )
}
