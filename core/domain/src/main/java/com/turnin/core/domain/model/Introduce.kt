package com.turnin.core.domain.model

import com.turnin.core.domain.common.validation.CommonValidationException

@JvmInline
value class Introduce private constructor(val value: String) {
    /**
     * 소개 글 VO
     *
     * @throws CommonValidationException 유효성 검사 실패 시
     */
    companion object {
        const val MIN_LENGTH = 0
        const val MAX_LENGTH = 200
        private const val FIELD = "소개 글"

        fun from(value: String): Introduce = Introduce(value)

        operator fun invoke(value: String): Introduce = from(value)
    }

    init {
        validate()
    }

    private fun validate() {
        when {
            // 1) 길이 제약 위반
            value.length !in MIN_LENGTH..MAX_LENGTH -> {
                throw CommonValidationException.TooShortOrLong(
                    field = FIELD,
                    min = MIN_LENGTH,
                    max = MAX_LENGTH,
                )
            }
        }
    }
}
