package com.turnin.core.domain.announcement.model

import com.turnin.core.domain.model.Role

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
        fun fromRole(role: Role): List<AnnouncementAudience> = when (role) {
            Role.USER -> listOf(ALL)
            else -> emptyList()
        }

        fun from(value: String): AnnouncementAudience = when (value) {
            "ALL" -> AnnouncementAudience.ALL
            else -> AnnouncementAudience.UNKNOWN
        }
    }
}
