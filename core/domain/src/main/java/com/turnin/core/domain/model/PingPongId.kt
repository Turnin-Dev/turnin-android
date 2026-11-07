package com.turnin.core.domain.model

@JvmInline
value class PingPongId private constructor(val value: Long) {
    /**
     * 핑퐁 ID VO
     */
    companion object {
        operator fun invoke(value: Long): PingPongId = PingPongId(value)
    }
}
