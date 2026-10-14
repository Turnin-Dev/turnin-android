package com.turnin.core.domain.block.model

import kotlin.jvm.JvmInline

/** 차단 사유 ID VO*/
@JvmInline
value class BlockReasonId private constructor(val value: Long) {
    /** 차단 사유 ID VO */
    companion object {
        operator fun invoke(value: Long): BlockReasonId = BlockReasonId(value)
    }

    init {
        validate()
    }

    fun validate() {
    }
}
