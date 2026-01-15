package com.peekr.domain.profile.error

import com.peekr.core.domain.common.BaseError
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.common.validation.ValidationErrorType

sealed interface ProfileErrorType : BaseError {
    /** 나의 사용자 ID 조회 실패 에러 */
    data object MyUserIdNotFound : ProfileErrorType

    /** 프로필 로드 실패 에러 */
    data object ProfileLoadFailed : ProfileErrorType

    /** 키워드 리스트 로드 실패 에러 */
    data object KeywordsLoadFailed : ProfileErrorType

    /** 알 수 없는 에러로 자세한 사항은 [cause] 파라미터에 [Throwable]형태로 담는다. */
    data class Unexpected(val cause: Throwable?) : ProfileErrorType

    // ------------------------------ 친구 상태 관련 에러 ------------------------------

    /** 친구 상태 변경 실패 에러 */
    data object UpdateFriendStatusFailed : ProfileErrorType

    /** 이미 친구거나 요청을 보낸 상태 */
    data object AlreadyFriendsOrRequested : ProfileErrorType

    /** 이미 처리된 요청 */
    data object AlreadyProcessed : ProfileErrorType

    // ------------------------------ Other Error Type ------------------------------
    data class CommonError(val error: CommonErrorType) : ProfileErrorType

    data class ValidationError(val error: ValidationErrorType) : ProfileErrorType
}
