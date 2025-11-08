package com.peekr.domain.keywordDetail.error

import com.peekr.core.domain.user.error.UserErrorType
import com.peekr.core.domain.userKeyword.error.UserKeywordErrorType
import com.peekr.core.domain.util.BaseError

sealed interface KeywordDetailErrorType : BaseError {
    /** 알 수 없는 에러로 자세한 사항은 [cause] 파라미터에 [Throwable]형태로 담는다. */
    data class Unexpected(val cause: Throwable?) : KeywordDetailErrorType

    // ------------------------------ Other Error Type ------------------------------
    data class UserKeywordError(val error: UserKeywordErrorType) : KeywordDetailErrorType

    data class UserError(val error: UserErrorType) : KeywordDetailErrorType
}
