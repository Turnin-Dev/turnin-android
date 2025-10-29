package com.peekr.domain.profile.error

import com.peekr.core.domain.user.error.UserErrorType
import com.peekr.core.domain.userKeyword.error.UserKeywordErrorType
import com.peekr.core.domain.util.BaseError
import com.peekr.core.domain.util.CommonErrorType
import com.peekr.core.domain.validation.ValidationErrorType

sealed interface ProfileErrorType : BaseError {
    data object UserNotFound : ProfileErrorType

    data object UpdateUserKeywordOffsetFailed : ProfileErrorType

    /** 알 수 없는 에러로 자세한 사항은 [cause] 파라미터에 [Throwable]형태로 담는다. */
    data class Unexpected(val cause: Throwable?) : ProfileErrorType

    // ------------------------------ Other Error Type ------------------------------
    data class CommonError(val error: CommonErrorType) : ProfileErrorType

    data class UserError(val error: UserErrorType) : ProfileErrorType

    data class UserKeywordError(val error: UserKeywordErrorType) : ProfileErrorType

    data class ValidationError(val error: ValidationErrorType) : ProfileErrorType
}
