package com.turnin.core.domain.pingPong.model

import com.turnin.core.domain.common.validation.CommonValidationException

@JvmInline
value class PingPongQuestionContent private constructor(val value: String) {
    /**
     * 핑퐁 질문 내용 VO
     *
     * 새 질문은 [create]로 생성해 질문 형식 규칙을 적용한다.
     * 생성자([invoke])는 공백/최대 글자 수만 검증하며, 이미 저장된 질문을 복원할 때 사용한다.
     * (형식 규칙 도입 전에 저장된 질문은 물음표로 끝나지 않을 수 있다.)
     *
     * @throws CommonValidationException
     */
    companion object {
        const val MIN_LENGTH = 1
        const val MAX_LENGTH = 2200
        const val QUESTION_MARK = '?'
        private const val FIELD = "핑퐁 질문"

        operator fun invoke(value: String): PingPongQuestionContent = PingPongQuestionContent(value)

        /**
         * 질문 형식 규칙을 적용해 새 질문 내용을 생성한다.
         *
         * 1. 앞뒤 공백을 제거한다.
         * 2. 끝의 물음표 하나를 뗀 본문이 비어 있으면 예외가 발생한다. (`"?"`는 불가, `"??"`는 가능)
         * 3. 마지막 글자가 물음표가 아니면 물음표를 하나 붙인다.
         * 4. 붙인 물음표를 포함한 길이로 최대 글자 수([MAX_LENGTH])를 검증한다.
         *
         * @param input 사용자가 입력한 질문 내용
         * @throws CommonValidationException 본문이 비어 있거나 최대 글자 수를 초과한 경우
         */
        fun create(input: String): PingPongQuestionContent {
            val trimmed = input.trim()
            if (trimmed.removeSuffix(QUESTION_MARK.toString()).isBlank()) {
                throw CommonValidationException.Empty(FIELD)
            }
            val normalized = if (trimmed.last() == QUESTION_MARK) trimmed else trimmed + QUESTION_MARK

            return PingPongQuestionContent(normalized)
        }
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
