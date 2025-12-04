package com.peekr.domain.profile.error

import com.peekr.core.domain.common.BaseError
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.common.validation.ValidationErrorType

sealed interface ProfileErrorType : BaseError {
    data object UserNotFound : ProfileErrorType

    data object UpdateUserKeywordOffsetFailed : ProfileErrorType

    data object ProfileLoadFailed : ProfileErrorType

    /** 알 수 없는 에러로 자세한 사항은 [cause] 파라미터에 [Throwable]형태로 담는다. */
    data class Unexpected(val cause: Throwable?) : ProfileErrorType

    // ------------------------------ Other Error Type ------------------------------
    data class CommonError(val error: CommonErrorType) : ProfileErrorType

    data class ValidationError(val error: ValidationErrorType) : ProfileErrorType
}
