package com.turnin.shared.feature.announcement.error

import com.turnin.shared.core.error.NetworkErrorType
import com.turnin.shared.core.result.BaseError

sealed interface AnnouncementErrorType : BaseError {
    /** 알 수 없는 에러로 자세한 사항은 [cause] 파라미터에 [Throwable]형태로 담는다. */
    data class Unexpected(val cause: Throwable?) : AnnouncementErrorType

    // ------------------------------ Other Error Type ------------------------------
    data class Network(val error: NetworkErrorType) : AnnouncementErrorType
}
