package com.peekr.domain.friend.error

import com.peekr.core.domain.common.BaseError

sealed interface FriendErrorType : BaseError {
    /** 사용자 ID 조회 실패 에러 */
    data object UserIdNotFound : FriendErrorType

    /** 알 수 없는 에러로 자세한 사항은 [cause] 파라미터에 [Throwable]형태로 담는다. */
    data class Unexpected(val cause: Throwable?) : FriendErrorType
}
