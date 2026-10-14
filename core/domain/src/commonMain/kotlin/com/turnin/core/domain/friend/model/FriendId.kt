package com.turnin.core.domain.friend.model

import kotlin.jvm.JvmInline

@JvmInline
value class FriendId private constructor(val value: Long) {
    /** 친구 ID */
    companion object {
        operator fun invoke(value: Long): FriendId = FriendId(value)
    }

    init {
        validate()
    }

    fun validate() {
    }
}
