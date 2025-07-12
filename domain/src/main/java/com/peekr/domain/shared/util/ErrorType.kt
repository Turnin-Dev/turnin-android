package com.peekr.domain.shared.util

sealed interface ErrorType : Error {
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
}
