package com.peekr.domain.keywordDetail.error

import com.peekr.core.domain.common.BaseError
import com.peekr.core.domain.common.CommonErrorType

sealed interface KeywordDetailErrorType : BaseError {
    data object UserIdNotFound : KeywordDetailErrorType

    /** 알 수 없는 에러로 자세한 사항은 [cause] 파라미터에 [Throwable]형태로 담는다. */
    data class Unexpected(val cause: Throwable?) : KeywordDetailErrorType

    // ------------------------------ Other Error Type ------------------------------
    data class CommonError(val error: CommonErrorType) : KeywordDetailErrorType
}
