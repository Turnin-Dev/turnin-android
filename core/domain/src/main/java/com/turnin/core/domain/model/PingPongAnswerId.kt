package com.turnin.core.domain.model

@JvmInline
value class PingPongAnswerId private constructor(val value: Long) {
    /**
     * 핑퐁 답변 ID VO
     */
    companion object {
        operator fun invoke(value: Long): PingPongAnswerId = PingPongAnswerId(value)
    }
}
