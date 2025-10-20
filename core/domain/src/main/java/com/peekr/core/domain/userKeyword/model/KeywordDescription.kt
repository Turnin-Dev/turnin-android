package com.peekr.core.domain.userKeyword.model

/**
 * 키워드 내용
 */
@JvmInline
value class KeywordDescription private constructor(val value: String) {
    companion object {
        operator fun invoke(value: String): KeywordDescription = KeywordDescription(value)
    }
}
