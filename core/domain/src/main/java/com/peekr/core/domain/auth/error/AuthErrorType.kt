package com.peekr.core.domain.auth.error

import com.peekr.core.domain.common.BaseError
import com.peekr.core.domain.common.CommonErrorType

sealed interface AuthErrorType : BaseError {
    /** ID 토큰 파싱 에러 */
    data object IdTokenParsing : AuthErrorType

    /** 인증 로직 취소 에러 */
    data object Cancellation : AuthErrorType

    /** 유효하지 않은 토큰 타입 에러 */
    data object TokenTypeInvalid : AuthErrorType

    /** 사용자를 찾을 수 없는 에러 */
    data object UserNotFound : AuthErrorType

    /** 계정 삭제 실패 에러 */
    data object DeleteAccountFailed : AuthErrorType

    /** 카카오 로그인 에러 */
    data object KakaoSignInError : AuthErrorType

    /** 카카오 로그아웃 에러 */
    data object KakaoSignOutError : AuthErrorType

    /** 카카오 계정 삭제 에러 */
    data object KakaoDeleteAccountError : AuthErrorType

    /** 토큰 저장 실패 에러 */
    data object SaveTokenFailed : AuthErrorType

    /** 로그인 실패 에러 */
    data object LoginFailed : AuthErrorType

    /** 알 수 없는 에러로 자세한 사항은 [cause] 파라미터에 [Throwable]형태로 담는다. */
    data class Unexpected(val cause: Throwable?) : AuthErrorType

    // ------------------------------ Other Error Type ------------------------------
    data class CommonError(val error: CommonErrorType) : AuthErrorType
}
