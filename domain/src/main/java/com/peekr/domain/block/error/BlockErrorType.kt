package com.peekr.domain.block.error

import com.peekr.core.domain.common.BaseError
import com.peekr.core.domain.common.error.CommonErrorType

sealed interface BlockErrorType : BaseError {
    /** 차단 요청자과 blockerId가 일치하지 않는 경우 */
    data object RequesterIdBlockerIdNotSame : BlockErrorType

    /** 알 수 없는 에러로 자세한 사항은 [cause] 파라미터에 [Throwable]형태로 담는다. */
    data class Unexpected(val cause: Throwable?) : BlockErrorType

    // ------------------------------ Error Mapping ------------------------------
    data class CommonError(val error: CommonErrorType) : BlockErrorType
}
