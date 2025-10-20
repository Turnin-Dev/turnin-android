package com.peekr.core.domain.model

@JvmInline
value class KeywordDescription private constructor(val value: String) {
    /**
     * 키워드 내용
     */
    companion object {
        operator fun invoke(value: String): KeywordDescription = KeywordDescription(value)
    }
}
