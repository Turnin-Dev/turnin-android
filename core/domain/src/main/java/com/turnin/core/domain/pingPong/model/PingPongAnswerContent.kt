package com.turnin.core.domain.pingPong.model

import com.turnin.core.domain.common.validation.CommonValidationException

@JvmInline
value class PingPongAnswerContent private constructor(val value: String) {
    /**
     * 핑퐁 답변 내용 VO
     *
     * @throws CommonValidationException
     */
    companion object {
        const val MIN_LENGTH = 1
        const val MAX_LENGTH = 2200
        private const val FIELD = "핑퐁 답변"

        operator fun invoke(value: String): PingPongAnswerContent = PingPongAnswerContent(value)
    }

    init {
        validate()
    }

    private fun validate() {
        if (value.isBlank()) {
            throw CommonValidationException.Empty(FIELD)
        }
        if (value.length !in MIN_LENGTH..MAX_LENGTH) {
            throw CommonValidationException.TooShortOrLong(
                field = FIELD,
                min = MIN_LENGTH,
                max = MAX_LENGTH,
            )
        }
    }
}
