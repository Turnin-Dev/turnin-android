package com.turnin.core.domain.model

/** 사용자 ID VO */
@JvmInline
value class UserId private constructor(val value: Long) {
    /** 사용자 ID VO */
    companion object {
        fun from(value: Long): UserId = UserId(value)

        operator fun invoke(value: Long): UserId = from(value)
    }
}
