package com.peekr.domain.login.error

import com.peekr.core.domain.common.BaseError
import com.peekr.core.domain.common.error.CommonErrorType

sealed interface LoginErrorType : BaseError {
    /** 로그인 실패 에러 */
    data object LoginFailed : LoginErrorType

    /** 알 수 없는 에러로 자세한 사항은 [cause] 파라미터에 [Throwable]형태로 담는다. */
    data class Unexpected(val cause: Throwable?) : LoginErrorType

    // ------------------------------ Error Mapping ------------------------------
    data class CommonError(val error: CommonErrorType) : LoginErrorType
}
