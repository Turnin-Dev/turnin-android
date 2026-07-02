package com.turnin.core.domain.announcement.model

/**
 * 공지 수신 대상
 */
enum class AnnouncementAudience {
    /** 전체 대상 */
    ALL,

    /** 폴백 값 */
    UNKNOWN,
    ;

    companion object {
        fun from(value: String): AnnouncementAudience = when (value) {
            "ALL" -> AnnouncementAudience.ALL
            else -> AnnouncementAudience.UNKNOWN
        }
    }
}
