package com.peekr.core.domain.model

/** FCM 토큰 ID VO */
@JvmInline
value class FcmTokenId private constructor(val value: Long) {
    /** FCM 토큰 ID VO */
    companion object {
        fun from(value: Long): FcmTokenId = FcmTokenId(value)

        operator fun invoke(value: Long): FcmTokenId = from(value)
    }

    init {
        validate()
    }

    private fun validate() {
    }
}
