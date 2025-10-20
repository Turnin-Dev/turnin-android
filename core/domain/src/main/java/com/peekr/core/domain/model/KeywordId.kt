package com.peekr.core.domain.model

@JvmInline
value class KeywordId private constructor(val value: Long) {
    /**
     * 키워드 ID VO
     */
    companion object {
        fun from(value: Long): KeywordId = KeywordId(value)

        operator fun invoke(value: Long): KeywordId = from(value)
    }

    init {
        validate()
    }

    private fun validate() {
    }
}
