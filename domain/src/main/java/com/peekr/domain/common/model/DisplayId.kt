package com.peekr.domain.common.model

import com.peekr.domain.common.util.ValidationError

/** [DisplayId] 커스텀 예외 */
sealed class DisplayIdException(message: String) : IllegalArgumentException(message) {
    data class Empty(
        val msg: String = "사용자 표시 ID가 비어있습니다.",
    ) : DisplayIdException(msg)

    data class TooShortOrLong(
        val min: Int,
        val max: Int,
        val msg: String = "사용자 표시 ID는 $min~${max}자 이내만 가능합니다.",
    ) : DisplayIdException(msg)

    data class InvalidFormat(
        val format: String,
        val msg: String = "사용자 표시 ID는 ${format}만 가능합니다.",
    ) : DisplayIdException(msg)
}

fun DisplayIdException.toValidationError(): ValidationError = when (this) {
    is DisplayIdException.Empty -> ValidationError.DisplayId.Empty
    is DisplayIdException.TooShortOrLong -> {
        ValidationError.DisplayId.TooShortOrLong(this.min, this.max)
    }

    is DisplayIdException.InvalidFormat -> {
        ValidationError.DisplayId.InvalidFormat(this.format)
    }
}

/**
 * 사용자 표시 ID VO
 */
@JvmInline
value class DisplayId private constructor(val value: String) {
    companion object {
        const val MIN_LENGTH = 1
        const val MAX_LENGTH = 30

        /** 사용자 표시 ID 규칙: 영어/숫자/밑줄만 허용 */
        val regex = Regex("^[a-zA-Z0-9_]+$")

        fun from(value: String): DisplayId = DisplayId(value)

        operator fun invoke(value: String): DisplayId = from(value)
    }

    init {
        validate()
    }

    private fun validate() {
        when {
            // 1) 비어 있거나 공백인 경우
            value.isBlank() -> throw DisplayIdException.Empty()
            // 2) 길이 범위 위반
            value.length !in MIN_LENGTH..MAX_LENGTH -> {
                throw DisplayIdException.TooShortOrLong(min = MIN_LENGTH, max = MAX_LENGTH)
            }
            // 3) 허용 문자 위반
            !value.matches(regex) -> {
                throw DisplayIdException.InvalidFormat(format = "영어/숫자/밑줄(_)")
            }
        }
    }
}
