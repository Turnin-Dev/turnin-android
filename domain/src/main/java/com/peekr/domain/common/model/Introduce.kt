package com.peekr.domain.common.model

import com.peekr.domain.common.util.ValidationError

/** [Introduce] 커스텀 예외 */
sealed class IntroduceException(message: String) : IllegalArgumentException(message) {
    data class TooLong(
        val max: Int,
        val msg: String = "소개 글은 ${max}자 이내만 가능합니다.",
    ) : IntroduceException(msg)
}

fun IntroduceException.toValidationError(): ValidationError = when (this) {
    is IntroduceException.TooLong -> ValidationError.Introduce.TooLong(this.max)
}

/** 소개 글 VO */
@JvmInline
value class Introduce private constructor(val value: String) {
    companion object {
        const val MAX_LENGTH = 200

        fun from(value: String): Introduce = Introduce(value)

        operator fun invoke(value: String): Introduce = from(value)
    }

    init {
        validate()
    }

    private fun validate() {
        when {
            // 1) 길이 제약 위반
            value.length > MAX_LENGTH -> throw IntroduceException.TooLong(max = MAX_LENGTH)
        }
    }
}
