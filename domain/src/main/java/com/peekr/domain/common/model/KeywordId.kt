package com.peekr.domain.common.model

@JvmInline
value class KeywordId private constructor(val value: Long) {
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
