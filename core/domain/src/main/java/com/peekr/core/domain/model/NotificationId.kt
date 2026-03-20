package com.peekr.core.domain.model

/** 알림 ID VO */
@JvmInline
value class NotificationId private constructor(val value: Long) {
    /** 알림 ID VO */
    companion object {
        fun from(value: Long): NotificationId = NotificationId(value)

        operator fun invoke(value: Long): NotificationId = from(value)
    }

    init {
        validate()
    }

    private fun validate() {
    }
}
