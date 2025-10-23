package com.peekr.core.domain.validation

import com.peekr.core.domain.util.BaseError

sealed interface ValidationErrorType : BaseError {
    sealed interface Common : ValidationErrorType {
        /** [field]가 비어있는 경우 */
        data class Empty(val field: String) : Common

        /** [field]가 [min]보다 짧거나 [max]보다 긴 경우 */
        data class TooShortOrLong(
            val field: String,
            val min: Int,
            val max: Int,
        ) : Common

        /** [field]가 [format]과 일치하지 않는 경우 */
        data class InvalidFormat(
            val field: String,
            val format: String,
        ) : Common
    }
}
