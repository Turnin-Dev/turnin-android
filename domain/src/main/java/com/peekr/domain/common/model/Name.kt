package com.peekr.domain.common.model

import com.peekr.domain.common.util.ValidationError

/** [Name] 커스텀 예외 */
sealed class NameException(message: String) : IllegalArgumentException(message) {
    data class Empty(
        val msg: String = "이름이 비어있습니다.",
    ) : NameException(msg)

    data class TooShortOrLong(
        val min: Int,
        val max: Int,
        val msg: String = "이름은 $min~${max}자 이내만 가능합니다.",
    ) : NameException(msg)

    data class InvalidFormat(
        val format: String,
        val msg: String = "이름은 ${format}만 가능합니다.",
    ) : NameException(msg)
}

fun NameException.toValidationError(): ValidationError = when (this) {
    is NameException.Empty -> ValidationError.Name.Empty
    is NameException.TooShortOrLong -> {
        ValidationError.Name.TooShortOrLong(this.min, this.max)
    }

    is NameException.InvalidFormat -> {
        ValidationError.Name.InvalidFormat(this.format)
    }
}

/** 이름 VO */
@JvmInline
value class Name private constructor(val value: String) {
    companion object {
        const val MIN_LENGTH = 1
        const val MAX_LENGTH = 30

        /** 사용자 이름 규칙: 영어/숫자/한글만 허용 */
        val regex = Regex("^[a-zA-Z0-9가-힣]+$")

        fun from(value: String): Name = Name(value)

        operator fun invoke(value: String): Name = from(value)
    }

    init {
        validate()
    }

    private fun validate() {
        when {
            // 1) 비어 있거나 공백인 경우
            value.isBlank() -> throw NameException.Empty()
            // 2) 길이 범위 위반
            value.length !in MIN_LENGTH..MAX_LENGTH -> {
                throw NameException.TooShortOrLong(min = MIN_LENGTH, max = MAX_LENGTH)
            }
            // 3) 허용 문자 위반
            !value.matches(regex) -> {
                throw NameException.InvalidFormat(format = "영어/숫자/한글")
            }
        }
    }
}
