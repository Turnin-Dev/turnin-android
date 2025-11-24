package com.peekr.core.domain.model

import com.peekr.core.domain.common.validation.CommonValidationException

@JvmInline
value class DisplayId private constructor(val value: String) {
    /**
     * 사용자 표시 ID VO
     *
     * @throws CommonValidationException 유효성 검사 실패 시
     */
    companion object {
        const val MIN_LENGTH = 1
        const val MAX_LENGTH = 30
        private const val FIELD = "사용자 표시 ID"

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
            value.isBlank() -> throw CommonValidationException.Empty(FIELD)
            // 2) 길이 범위 위반
            value.length !in MIN_LENGTH..MAX_LENGTH -> {
                throw CommonValidationException.TooShortOrLong(
                    field = FIELD,
                    min = MIN_LENGTH,
                    max = MAX_LENGTH,
                )
            }
            // 3) 허용 문자 위반
            !value.matches(regex) -> {
                throw CommonValidationException.InvalidFormat(FIELD, "영어/숫자/밑줄(_)")
            }
        }
    }
}
