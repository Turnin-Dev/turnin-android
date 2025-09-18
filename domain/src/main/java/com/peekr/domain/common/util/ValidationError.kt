package com.peekr.domain.common.util

/**
 * 유효성 검사 에러 타입
 */
sealed interface ValidationError {
    sealed interface DisplayId : ValidationError {
        data object Empty : DisplayId

        data class TooShortOrLong(
            val min: Int,
            val max: Int,
        ) : DisplayId

        data class InvalidFormat(val format: String) : DisplayId
    }

    sealed interface Name : ValidationError {
        data object Empty : Name

        data class TooShortOrLong(
            val min: Int,
            val max: Int,
        ) : Name

        data class InvalidFormat(val format: String) : Name
    }

    sealed interface Introduce : ValidationError {
        data class TooLong(val max: Int) : Introduce
    }
}
