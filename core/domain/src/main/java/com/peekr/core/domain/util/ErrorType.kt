package com.peekr.core.domain.util

/** 앱 전체에서 사용되는 에러 타입으로 해당 에러 타입들은 클라이언트를 위한 에러 타입이다. */
sealed interface ErrorType : DomainError {
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

        // Token
        SaveTokenFailed,

        // Login
        LoginFailed,
    }

    sealed interface Validation : ErrorType {
        data class Empty(val field: String) : Validation

        data class InvalidFormat(
            val field: String,
            val format: String,
        ) : Validation

        data class TooShortOrLong(
            val field: String,
            val min: Int,
            val max: Int,
        ) : Validation
    }

    /** 네트워크에서 발생한 에러 타입 */
    enum class Network : ErrorType {
        /** 허가되지 않은 인증 */
        Unauthorized, // 401

        /** 클라이언트에서 발생한 에러 */
        ClientError,

        /** 서버 상에서 발생한 에러 */
        ServerError,

        /** 서버 및 네트워크 연결 에러 */
        ConnectionFailed,

        /** 지원하지 않는 파일 유형 */
        InvalidFileType,

        /** 파일 업로드 실패 에러 */
        UploadFileFailed,
    }

    /** 예외 타입 */
    enum class Exception : ErrorType {
        Json,
        TimeOut,
        IO,
    }

    /** 알 수 없는 에러로 자세한 사항은 [cause] 파라미터에 [Throwable]형태로 담는다. */
    data class Unexpected(val cause: Throwable?) : ErrorType
}
