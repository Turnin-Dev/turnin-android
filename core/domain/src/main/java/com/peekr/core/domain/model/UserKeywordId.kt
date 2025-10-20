package com.peekr.core.domain.model

@JvmInline
value class UserKeywordId private constructor(val value: Long) {
    /** 사용자 키워드 ID VO */
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
