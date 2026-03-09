package com.peekr.domain.setting.error

import com.peekr.core.domain.common.BaseError
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.common.validation.ValidationErrorType

sealed interface SettingErrorType : BaseError {
    /** 나의 프로필 정보 조회 실패 */
    data object MyProfileNotFound : SettingErrorType

    /** 사용자 표시 ID를 사용할 수 없는 에러 */
    data object DisplayIdNotAvailable : SettingErrorType

    /** 사진 업로드에 실패하는 경우 */
    data object UploadImageFailed : SettingErrorType

    /** 로그인 타입 조회 실패 에러 */
    data object LoginProviderNotFound : SettingErrorType

    /** 알 수 없는 에러로 자세한 사항은 [cause] 파라미터에 [Throwable]형태로 담는다. */
    data class Unexpected(val cause: Throwable?) : SettingErrorType

    // ------------------------------ Error Mapping ------------------------------
    data class CommonError(val error: CommonErrorType) : SettingErrorType

    data class ValidationError(val error: ValidationErrorType) : SettingErrorType
}
