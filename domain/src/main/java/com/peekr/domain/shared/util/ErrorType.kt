package com.peekr.domain.shared.util

sealed interface ErrorType : Error {
    enum class Auth : ErrorType {
        IdTokenParsing,
        Cancellation,
        TokenTypeInvalid,
        UserNotFound,
        DeleteAccountFailed,
        Unexpected,
    }
}
