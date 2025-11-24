package com.peekr.core.data.network.error

import com.peekr.core.domain.common.BaseError

sealed interface NetworkErrorType : BaseError {
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
