package com.peekr.domain.common.model

/** [Name] 커스텀 예외 */
sealed class NameException(message: String) : IllegalArgumentException(message) {
    data class Empty(
        val msg: String = "이름이 비어있습니다.",
    ) : NameException(msg)

    data class TooShortOrLong(
        val msg: String = "이름은 ${Name.MIN_LENGTH}~${Name.MAX_LENGTH}자 이내만 가능합니다.",
        val min: Int,
        val max: Int,
    ) : NameException(msg)

    data class InvalidFormat(
        val msg: String = "이름은 영어/숫자/한글만 가능합니다.",
        val format: String,
    ) : NameException(msg)
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
            value.isBlank() -> throw NameException.Empty()
            value.length !in MIN_LENGTH..MAX_LENGTH -> {
                throw NameException.TooShortOrLong(min = MIN_LENGTH, max = MAX_LENGTH)
            }

            !value.matches(regex) -> {
                throw NameException.InvalidFormat(format = "영어/숫자/한글")
            }
        }
    }
}
