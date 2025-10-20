package com.peekr.core.domain.user.model

/** 사용자 ID VO */
@JvmInline
value class UserId private constructor(val value: Long) {
    companion object {
        fun from(value: Long): UserId = UserId(value)

        operator fun invoke(value: Long): UserId = from(value)
    }

    init {
        validate()
    }

    private fun validate() {
    }
}
