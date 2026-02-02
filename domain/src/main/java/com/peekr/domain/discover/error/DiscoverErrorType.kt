package com.peekr.domain.discover.error

import com.peekr.core.domain.common.BaseError

sealed interface DiscoverErrorType : BaseError {
    data object MyProfileNotFound : DiscoverErrorType

    /** 알 수 없는 에러로 자세한 사항은 [cause] 파라미터에 [Throwable]형태로 담는다. */
    data class Unexpected(val cause: Throwable?) : DiscoverErrorType
}
