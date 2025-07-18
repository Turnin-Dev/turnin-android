package com.peekr.domain.shared.util

/** 앱 전체에서 사용되는 에러 타입으로 해당 에러 타입들은 클라이언트를 위한 에러 타입이다. */
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

    /** 네트워크에서 발생한 에러 타입 */
    enum class Network : ErrorType {
        EmptyResponse,
        BadRequest,
        Unauthorized,
        Forbidden,
        NotFound,
        RequestTimeout,
        Conflict,
        InternalServerError,
        BadGateway,
        ServiceUnavailable,
        HttpError,
        GatewayTimeout,
    }

    /** 예외 타입 */
    enum class Exception : ErrorType {
        Json,
        TimeOut,
        IO,
        Unexpected,
    }
}
