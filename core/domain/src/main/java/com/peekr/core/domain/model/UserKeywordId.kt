package com.peekr.core.domain.model

/** 사용자 키워드 ID VO */
@JvmInline
value class UserKeywordId private constructor(val value: Long) {
    companion object {
        fun from(value: Long): UserKeywordId = UserKeywordId(value)

        operator fun invoke(value: Long): UserKeywordId = from(value)
    }

    init {
        validate()
    }

    private fun validate() {
    }
}
