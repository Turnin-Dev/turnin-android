package com.peekr.core.data.network.util

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
        ConnectionFailed,
        InvalidFileType,
        UploadFileFailed,
    }

    /** Exception 에러 타입 */
    enum class Exception : NetworkErrorType {
        IO,
        TimeOut,
        JsonData,
        JsonEncoding,
        MalformedJson,
    }

    data class Unexpected(val cause: Throwable?) : NetworkErrorType
}
