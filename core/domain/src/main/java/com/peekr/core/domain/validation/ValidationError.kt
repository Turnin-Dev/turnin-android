package com.peekr.core.domain.validation

import com.peekr.core.domain.util.ErrorType

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

fun CommonValidationError.toErrorType(): ErrorType = when (this) {
    is CommonValidationError.Empty -> {
        ErrorType.Profile.ValidationError(
            message = "${this.field}는 비어있을 수 없습니다.",
        )
    }

    is CommonValidationError.InvalidFormat -> {
        ErrorType.Profile.ValidationError(
            message = "${this.field}는 ${this.format}만 가능합니다.",
        )
    }

    is CommonValidationError.TooShortOrLong -> {
        ErrorType.Profile.ValidationError(
            message = "${this.field}는 ${this.min} ~ ${this.max}자 이내만 가능합니다.",
        )
    }
}
