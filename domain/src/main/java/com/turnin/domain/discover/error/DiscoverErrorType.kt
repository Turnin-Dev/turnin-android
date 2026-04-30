package com.turnin.domain.discover.error

import com.turnin.core.domain.common.BaseError
import com.turnin.core.domain.common.error.CommonErrorType

sealed interface DiscoverErrorType : BaseError {
    /** 나의 프로필 조회 에러 */
    data object MyProfileNotFound : DiscoverErrorType

    /** 나의 키워드 새로고침 실패 */
    data object MyKeywordsRefreshFailed : DiscoverErrorType

    /** 탐색할 대상이 없는 경우 발생하는 에러 */
    data object NotSelectedTarget : DiscoverErrorType

    /** 알 수 없는 에러로 자세한 사항은 [cause] 파라미터에 [Throwable]형태로 담는다. */
    data class Unexpected(val cause: Throwable?) : DiscoverErrorType

    // ------------------------------ Other Error Type ------------------------------
    data class CommonError(val error: CommonErrorType) : DiscoverErrorType
}
