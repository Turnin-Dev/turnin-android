package com.peekr.domain.report.error

import com.peekr.core.domain.common.BaseError
import com.peekr.core.domain.common.error.CommonErrorType

sealed interface ReportErrorType : BaseError {
    /** 사용자 ID를 찾을 수 없는 경우 */
    data object UserIdNotFound : ReportErrorType

    /** 신고 사유를 선택하지 않았을 경우 */
    data object NotSelectedReportReason : ReportErrorType

    /** 신고 대상 사용자 ID를 선택하지 않았을 경우 */
    data object NotSelectedReportedId : ReportErrorType

    /** 필수 신고 대상이 누락된 경우 (신고 대상이 모두 null인 경우) */
    data object MissingReportTarget : ReportErrorType

    /** 신고 대상을 이미 신고한 경우 */
    data object AlreadyReported : ReportErrorType

    /** 알 수 없는 에러로 자세한 사항은 [cause] 파라미터에 [Throwable]형태로 담는다. */
    data class Unexpected(val cause: Throwable?) : ReportErrorType

    // ------------------------------ Error Mapping ------------------------------
    data class CommonError(val error: CommonErrorType) : ReportErrorType
}
