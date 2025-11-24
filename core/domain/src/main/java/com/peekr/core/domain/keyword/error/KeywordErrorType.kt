package com.peekr.core.domain.keyword.error

import com.peekr.core.domain.common.BaseError
import com.peekr.core.domain.common.CommonErrorType

sealed interface KeywordErrorType : BaseError {
    /** 알 수 없는 에러로 자세한 사항은 [cause] 파라미터에 [Throwable]형태로 담는다. */
    data class Unexpected(val cause: Throwable?) : KeywordErrorType

    // ------------------------------ Other Error Type ------------------------------
    data class CommonError(val error: CommonErrorType) : KeywordErrorType
}
