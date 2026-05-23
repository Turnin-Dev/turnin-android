package com.turnin.core.domain.common.validation

/** 공통 유효성 검사 예외 */
sealed class CommonValidationException(message: String) : IllegalArgumentException(message) {
    /** [field]가 비어있는 경우 */
    data class Empty(val field: String) : CommonValidationException(field)

    /** [field]가 [min]보다 짧거나 [max]보다 긴 경우 */
    data class TooShortOrLong(
        val field: String,
        val min: Int,
        val max: Int,
    ) : CommonValidationException(field)

    /** [field]가 [format]과 일치하지 않는 경우 */
    data class InvalidFormat(
        val field: String,
        val format: String,
    ) : CommonValidationException(field)

    /** 앞 뒤 공백이 있는 경우 */
    data class Whitespace(
        val field: String,
    ) : CommonValidationException(field)
}

fun CommonValidationException.toValidationErrorType(): ValidationErrorType = when (this) {
    is CommonValidationException.Empty -> {
        ValidationErrorType.Common.Empty(field)
    }

    is CommonValidationException.TooShortOrLong -> {
        ValidationErrorType.Common.TooShortOrLong(field, min, max)
    }

    is CommonValidationException.InvalidFormat -> {
        ValidationErrorType.Common.InvalidFormat(field, format)
    }

    is CommonValidationException.Whitespace -> {
        ValidationErrorType.Common.Whitespace(field)
    }
}
