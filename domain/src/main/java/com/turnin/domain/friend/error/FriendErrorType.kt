package com.turnin.domain.friend.error

import com.turnin.core.domain.common.BaseError
import com.turnin.core.domain.common.error.CommonErrorType

sealed interface FriendErrorType : BaseError {
    /** 나의 사용자 ID 조회 실패 에러 */
    data object MyUserIdNotFound : FriendErrorType

    /** 사용자 ID 조회 실패 에러 */
    data object UserIdNotFound : FriendErrorType

    /** 이미 처리된 요청인 경우이거나 사용자를 찾을 수 없는 경우 */
    data object AlreadyProceedOrUserNotFound : FriendErrorType

    /** 친구 상태 수정 요청자의 ID와 나의 ID가 같지 않은 경우 */
    data object NotSameRequesterIdAndMyId : FriendErrorType

    /** 알 수 없는 에러로 자세한 사항은 [cause] 파라미터에 [Throwable]형태로 담는다. */
    data class Unexpected(val cause: Throwable?) : FriendErrorType

    // ------------------------------ Other Error Type ------------------------------
    data class CommonError(val error: CommonErrorType) : FriendErrorType
}
