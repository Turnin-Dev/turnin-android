package com.peekr.domain.report.error

import com.peekr.core.domain.common.BaseError
import com.peekr.core.domain.common.error.CommonErrorType

sealed interface ReportErrorType : BaseError {
    data object UserIdNotFound : ReportErrorType

    data object NotSelectedReportReason : ReportErrorType

    data object NotSelectedReportedId : ReportErrorType

    /** 알 수 없는 에러로 자세한 사항은 [cause] 파라미터에 [Throwable]형태로 담는다. */
    data class Unexpected(val cause: Throwable?) : ReportErrorType

    // ------------------------------ Error Mapping ------------------------------
    data class CommonError(val error: CommonErrorType) : ReportErrorType
}
