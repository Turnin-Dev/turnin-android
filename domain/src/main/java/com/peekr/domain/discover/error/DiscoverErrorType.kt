package com.peekr.domain.discover.error

import com.peekr.core.domain.common.BaseError

sealed interface DiscoverErrorType : BaseError {
    /** 나의 프로필 조회 에러 */
    data object MyProfileNotFound : DiscoverErrorType

    /** 탐색할 대상이 없는 경우 발생하는 에러 */
    data object NotSelectedTarget : DiscoverErrorType

    /** 알 수 없는 에러로 자세한 사항은 [cause] 파라미터에 [Throwable]형태로 담는다. */
    data class Unexpected(val cause: Throwable?) : DiscoverErrorType
}
