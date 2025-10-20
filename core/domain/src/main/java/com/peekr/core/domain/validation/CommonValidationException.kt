package com.peekr.core.domain.validation

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
}

fun CommonValidationException.toCommonValidationError(): CommonValidationError = when (this) {
    is CommonValidationException.Empty -> {
        CommonValidationError.Empty(field)
    }

    is CommonValidationException.TooShortOrLong -> {
        CommonValidationError.TooShortOrLong(field, min, max)
    }

    is CommonValidationException.InvalidFormat -> {
        CommonValidationError.InvalidFormat(field, format)
    }
}
