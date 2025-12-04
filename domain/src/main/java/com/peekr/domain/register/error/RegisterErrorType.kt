package com.peekr.domain.register.error

import com.peekr.core.domain.common.BaseError
import com.peekr.core.domain.common.error.CommonErrorType

sealed interface RegisterErrorType : BaseError {
    /** 사용자 표시 ID를 사용할 수 없는 에러 */
    data object DisplayIdNotAvailable : RegisterErrorType

    /** 빈칸은 허용하지 않는다는 에러 */
    data object CantUseEmptyOrBlank : RegisterErrorType

    /** 변환된 사진이 null인 상황에 대한 에러 */
    data object ImageFileIsNull : RegisterErrorType

    /** 알 수 없는 에러로 자세한 사항은 [cause] 파라미터에 [Throwable]형태로 담는다. */
    data class Unexpected(val cause: Throwable?) : RegisterErrorType

    // ------------------------------ Error Mapping ------------------------------
    data class CommonError(val error: CommonErrorType) : RegisterErrorType
}
