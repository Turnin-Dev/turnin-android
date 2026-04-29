package com.turnin.domain.keywordEdit.error

import com.turnin.core.domain.common.BaseError
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.common.validation.ValidationErrorType

sealed interface KeywordEditErrorType : BaseError {
    /** 나의 사용자 ID 조회 실패 에러 */
    data object MyUserIdNotFound : KeywordEditErrorType

    /** 알 수 없는 에러로 자세한 사항은 [cause] 파라미터에 [Throwable]형태로 담는다. */
    data class Unexpected(val cause: Throwable?) : KeywordEditErrorType

    /** 키워드 수정 실패 에러 */
    data object UpdateFailed : KeywordEditErrorType

    // ------------------------------ Other Error Type ------------------------------
    data class CommonError(val error: CommonErrorType) : KeywordEditErrorType

    data class ValidationError(val error: ValidationErrorType) : KeywordEditErrorType
}
