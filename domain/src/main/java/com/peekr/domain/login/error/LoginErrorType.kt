package com.peekr.domain.login.error

import com.peekr.core.domain.common.BaseError
import com.peekr.core.domain.common.error.CommonErrorType

sealed interface LoginErrorType : BaseError {
    /** ID 토큰 파싱 에러 */
    data object IdTokenParsing : LoginErrorType

    /** 인증 로직 취소 에러 */
    data object Cancellation : LoginErrorType

    /** 유효하지 않은 토큰 타입 에러 */
    data object TokenTypeInvalid : LoginErrorType

    /** 사용자를 찾을 수 없는 에러 */
    data object UserNotFound : LoginErrorType

    /** 계정 삭제 실패 에러 */
    data object DeleteAccountFailed : LoginErrorType

    /** 카카오 로그인 에러 */
    data object KakaoSignInError : LoginErrorType

    /** 카카오 로그아웃 에러 */
    data object KakaoSignOutError : LoginErrorType

    /** 카카오 계정 삭제 에러 */
    data object KakaoDeleteAccountError : LoginErrorType

    /** 토큰 저장 실패 에러 */
    data object SaveTokenFailed : LoginErrorType

    /** 로그인 실패 에러 */
    data object LoginFailed : LoginErrorType

    /** 알 수 없는 에러로 자세한 사항은 [cause] 파라미터에 [Throwable]형태로 담는다. */
    data class Unexpected(val cause: Throwable?) : LoginErrorType

    // ------------------------------ Error Mapping ------------------------------
    data class CommonError(val error: CommonErrorType) : LoginErrorType
}
