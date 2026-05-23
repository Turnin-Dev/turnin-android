package com.turnin.core.domain.model

import com.turnin.core.domain.common.validation.CommonValidationException

/**
 * 이름 VO
 */
@JvmInline
value class Name private constructor(val value: String) {
    /**
     * 이름 VO
     *
     * @throws CommonValidationException 유효성 검사 실패 시
     */
    companion object {
        const val MIN_LENGTH = 1
        const val MAX_LENGTH = 30
        private const val FIELD = "이름"

        /** 사용자 이름 규칙: 영어/숫자/한글/특수문자(. - ' _ ! ?) 허용, 공백 허용하되 시작·끝 불가 */
        val regex = Regex("^[a-zA-Z0-9가-힣 .\\-'_!?]+$")

        fun from(value: String): Name = Name(value)

        operator fun invoke(value: String): Name = from(value)
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
            // 2) 공백으로 시작하거나 끝나는 경우
            value.first().isWhitespace() || value.last().isWhitespace() -> {
                throw CommonValidationException.Whitespace(FIELD)
            }
            // 3) 길이 범위 위반
            value.length !in MIN_LENGTH..MAX_LENGTH -> {
                throw CommonValidationException.TooShortOrLong(
                    field = FIELD,
                    min = MIN_LENGTH,
                    max = MAX_LENGTH,
                )
            }
            // 4) 허용 문자 위반
            !value.matches(regex) -> {
                throw CommonValidationException.InvalidFormat(
                    field = FIELD,
                    format = "영어/숫자/한글/특수문자(. - ' _ ! ?)",
                )
            }
        }
    }
}
