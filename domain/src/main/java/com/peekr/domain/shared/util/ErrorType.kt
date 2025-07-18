package com.peekr.domain.shared.util

/** 앱 전체에서 사용되는 에러 타입 */
sealed interface ErrorType : Error {
    /** Auth 에러 타입 */
    enum class Auth : ErrorType {
        // Common
        IdTokenParsing,
        Cancellation,
        TokenTypeInvalid,
        UserNotFound,
        DeleteAccountFailed,

        // Kakao
        KakaoSignInError,
        KakaoSignOutError,
        KakaoDeleteAccountError,

        // Etc
        Unexpected,
    }

    /** 예외 타입 */
    enum class Exception : ErrorType {
        Unexpected,
    }
}
