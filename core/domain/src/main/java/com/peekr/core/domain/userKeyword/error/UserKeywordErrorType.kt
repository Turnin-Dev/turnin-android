package com.peekr.core.domain.userKeyword.error

import com.peekr.core.domain.util.BaseError
import com.peekr.core.domain.util.CommonErrorType

sealed interface UserKeywordErrorType : BaseError {
    /** 알 수 없는 에러로 자세한 사항은 [cause] 파라미터에 [Throwable]형태로 담는다. */
    data class Unexpected(val cause: Throwable?) : UserKeywordErrorType

    // ------------------------------ Other Error Type ------------------------------
    data class CommonError(val error: CommonErrorType) : UserKeywordErrorType
}
