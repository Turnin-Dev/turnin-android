package com.peekr.data.shared.util.network

import com.peekr.domain.shared.util.ErrorType

fun NetworkErrorType.toErrorType(): ErrorType = when (this) {
    // ------------------------------ Network ------------------------------
    NetworkErrorType.Network.EmptyResponse -> ErrorType.Network.EmptyResponse
    NetworkErrorType.Network.BadRequest -> ErrorType.Network.BadRequest
    NetworkErrorType.Network.Unauthorized -> ErrorType.Network.Unauthorized
    NetworkErrorType.Network.Forbidden -> ErrorType.Network.Forbidden
    NetworkErrorType.Network.NotFound -> ErrorType.Network.NotFound
    NetworkErrorType.Network.RequestTimeout -> ErrorType.Network.RequestTimeout
    NetworkErrorType.Network.InternalServerError -> ErrorType.Network.InternalServerError
    NetworkErrorType.Network.BadGateway -> ErrorType.Network.BadGateway
    NetworkErrorType.Network.ServiceUnavailable -> ErrorType.Network.ServiceUnavailable
    NetworkErrorType.Network.HttpError -> ErrorType.Network.HttpError
    // ------------------------------ Exception ------------------------------
    NetworkErrorType.Exception.IO -> ErrorType.Exception.IO
    NetworkErrorType.Exception.TimeOut -> ErrorType.Exception.TimeOut
    NetworkErrorType.Exception.JsonData -> ErrorType.Exception.Json
    NetworkErrorType.Exception.JsonEncoding -> ErrorType.Exception.Json
    NetworkErrorType.Exception.MalformedJson -> ErrorType.Exception.Json
    NetworkErrorType.Exception.Unexpected -> ErrorType.Exception.Unexpected
}
