package com.peekr.core.domain.validation

/** 유효성 검사 에러 타입 */
sealed interface ValidationError

/** 공통 유효성 검사 에러 타입 */
sealed interface CommonValidationError : ValidationError {
    /** [field]가 비어있는 경우 */
    data class Empty(val field: String) : CommonValidationError

    /** [field]가 [min]보다 짧거나 [max]보다 긴 경우 */
    data class TooShortOrLong(
        val field: String,
        val min: Int,
        val max: Int,
    ) : CommonValidationError

    /** [field]가 [format]과 일치하지 않는 경우 */
    data class InvalidFormat(
        val field: String,
        val format: String,
    ) : CommonValidationError
}
