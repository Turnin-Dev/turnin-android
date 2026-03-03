package com.peekr.domain.setting.error

import com.peekr.core.domain.common.BaseError
import com.peekr.core.domain.common.error.CommonErrorType

sealed interface SettingErrorType : BaseError {
    /** 로그인 타입을 찾을 수 없음 */
    data object LoginProviderNotFound : SettingErrorType

    /** 나의 프로필 정보 조회 실패 */
    data object MyProfileNotFound : SettingErrorType

    /** 알 수 없는 에러로 자세한 사항은 [cause] 파라미터에 [Throwable]형태로 담는다. */
    data class Unexpected(val cause: Throwable?) : SettingErrorType

    // ------------------------------ Error Mapping ------------------------------
    data class CommonError(val error: CommonErrorType) : SettingErrorType
}
