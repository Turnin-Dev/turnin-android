package com.turnin.domain.home.error

import com.turnin.core.domain.common.BaseError
import com.turnin.core.domain.common.error.CommonErrorType

sealed interface HomeErrorType : BaseError {
    /** 알 수 없는 에러로 자세한 사항은 [cause] 파라미터에 [Throwable]형태로 담는다. */
    data class Unexpected(val cause: Throwable?) : HomeErrorType

    // ------------------------------ Other Error Type ------------------------------
    data class CommonError(val error: CommonErrorType) : HomeErrorType
}
