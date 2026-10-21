package com.turnin.shared.data.core.network

/** 공유 모듈에서 호출하는 API 경로 */
internal object ApiPath {
    private const val VERSION = "/v1"
    private const val BASE = "/api$VERSION"

    object Auth {
        const val REFRESH = "$BASE/auth/refresh"
    }

    object User {
        const val LOGOUT = "$BASE/user/logout"
    }

    object Announcement {
        const val ROUTE = "$BASE/announcement"
        const val READ = "$ROUTE/read/{announcementId}"
    }
}
