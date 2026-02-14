package com.peekr.core.domain.model

/** 차단 ID VO */
@JvmInline
value class BlockId private constructor(val value: Long) {
    companion object {
        fun from(value: Long): BlockId = BlockId(value)

        operator fun invoke(value: Long): BlockId = from(value)
    }

    init {
        validate()
    }

    private fun validate() {
    }
}
