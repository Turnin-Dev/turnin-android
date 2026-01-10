package com.peekr.domain.keywordEdit.error

import com.peekr.core.domain.common.BaseError
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.common.validation.ValidationErrorType

sealed interface KeywordEditErrorType : BaseError {
    /** 나의 사용자 ID 조회 실패 에러 */
    data object MyUserIdNotFound : KeywordEditErrorType

    // ------------------------------ Other Error Type ------------------------------
    data class CommonError(val error: CommonErrorType) : KeywordEditErrorType

    data class ValidationError(val error: ValidationErrorType) : KeywordEditErrorType
}
