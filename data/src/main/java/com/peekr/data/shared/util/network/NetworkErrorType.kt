package com.peekr.data.shared.util.network

sealed interface NetworkErrorType {
    /** Network 에러 타입 */
    enum class Network : NetworkErrorType {
        EmptyResponse,
        BadRequest,
        Unauthorized,
        Forbidden,
        NotFound,
        Conflict,
        RequestTimeout,
        InternalServerError,
        BadGateway,
        ServiceUnavailable,
        HttpError,
        GatewayTimeout,
    }

    /** Exception 에러 타입 */
    enum class Exception : NetworkErrorType {
        IO,
        TimeOut,
        JsonData,
        JsonEncoding,
        MalformedJson,
        Unexpected,
    }
}
