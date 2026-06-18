package com.turnin.core.domain.model

/** 공지 ID VO */
@JvmInline
value class AnnouncementId private constructor(val value: Long) {
    companion object {
        fun from(value: Long): AnnouncementId = AnnouncementId(value)

        operator fun invoke(value: Long): AnnouncementId = from(value)
    }
}
