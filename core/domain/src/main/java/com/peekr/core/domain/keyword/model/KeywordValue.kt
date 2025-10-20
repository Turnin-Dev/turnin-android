package com.peekr.core.domain.keyword.model

import com.peekr.core.domain.validation.CommonValidationException

/**
 * 키워드 명(값)
 *
 * @throws CommonValidationException 유효성 검사 실패 시
 */
@JvmInline
value class KeywordValue private constructor(val value: String) {
    companion object {
        const val MIN_LENGTH = 1
        const val MAX_LENGTH = 15
        private const val FIELD = "키워드"

        fun from(value: String): KeywordValue = KeywordValue(value)

        operator fun invoke(value: String): KeywordValue = from(value)
    }

    init {
        validate()
    }

    private fun validate() {
        when {
            // 1) 비어 있거나 공백인 경우
            value.isBlank() -> {
                throw CommonValidationException.Empty(FIELD)
            }
            // 2) 길이 범위 위반
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
