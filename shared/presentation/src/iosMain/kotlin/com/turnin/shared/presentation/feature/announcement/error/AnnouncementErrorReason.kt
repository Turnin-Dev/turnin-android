package com.turnin.shared.presentation.feature.announcement.error

import com.turnin.shared.domain.core.error.NetworkErrorType
import com.turnin.shared.domain.feature.announcement.error.AnnouncementErrorType

/** 공지 요청 실패 사유 (Swift에서 문구를 정한다.) */
enum class AnnouncementErrorReason {
    /** 인증 필요 (토큰 없음·만료) */
    UNAUTHORIZED,

    /** 네트워크 연결·타임아웃 */
    NETWORK,

    /** 그 외 */
    UNKNOWN,
}

internal fun AnnouncementErrorType.toReason(): AnnouncementErrorReason = when (this) {
    is AnnouncementErrorType.Network -> when (error) {
        NetworkErrorType.Network.Unauthorized -> AnnouncementErrorReason.UNAUTHORIZED
        NetworkErrorType.Network.ConnectionFailed,
        NetworkErrorType.Exception.IO,
        NetworkErrorType.Exception.TimeOut,
        -> AnnouncementErrorReason.NETWORK

        else -> AnnouncementErrorReason.UNKNOWN
    }

    is AnnouncementErrorType.Unexpected -> AnnouncementErrorReason.UNKNOWN
}
