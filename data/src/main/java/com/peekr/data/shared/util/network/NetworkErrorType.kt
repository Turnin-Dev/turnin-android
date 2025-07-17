package com.peekr.data.shared.util.network

sealed interface NetworkErrorType {
    /** Network 에러 타입 */
    enum class Network : NetworkErrorType {
        EmptyResponse,
        BadRequest,
        Unauthorized,
        Forbidden,
        NotFound,
        RequestTimeout,
        InternalServerError,
        BadGateway,
        ServiceUnavailable,
        HttpError,
    }

    /** Exception 에러 타입 */
    enum class Exception : NetworkErrorType {
        IO,
        TIMEOUT,
        JSON_DATA,
        JSON_ENCODING,
        MALFORMED_JSON,
        Unexpected,
    }
}
