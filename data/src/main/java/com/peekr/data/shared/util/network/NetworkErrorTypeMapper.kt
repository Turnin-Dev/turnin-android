package com.peekr.data.shared.util.network

import com.peekr.domain.shared.util.ErrorType

fun NetworkErrorType.toErrorType(): ErrorType = when (this) {
    // ------------------------------ Exception ------------------------------
    NetworkErrorType.Exception.IO -> ErrorType.Exception.IO
    NetworkErrorType.Exception.TimeOut -> ErrorType.Exception.TimeOut
    NetworkErrorType.Exception.JsonData -> ErrorType.Exception.Json
    NetworkErrorType.Exception.JsonEncoding -> ErrorType.Exception.Json
    NetworkErrorType.Exception.MalformedJson -> ErrorType.Exception.Json
    NetworkErrorType.Exception.Unexpected -> ErrorType.Exception.Unexpected
    // ------------------------------ Network ------------------------------
    NetworkErrorType.Network.EmptyResponse -> ErrorType.Network.Unexpected
    NetworkErrorType.Network.Unauthorized -> ErrorType.Network.Unauthorized
    NetworkErrorType.Network.BadRequest -> ErrorType.Network.ClientError
    NetworkErrorType.Network.Forbidden -> ErrorType.Network.ClientError
    NetworkErrorType.Network.NotFound -> ErrorType.Network.ClientError
    NetworkErrorType.Network.RequestTimeout -> ErrorType.Network.ClientError
    NetworkErrorType.Network.HttpError -> ErrorType.Network.ClientError
    NetworkErrorType.Network.Conflict -> ErrorType.Network.ClientError
    NetworkErrorType.Network.InternalServerError -> ErrorType.Network.ServerError
    NetworkErrorType.Network.BadGateway -> ErrorType.Network.ServerError
    NetworkErrorType.Network.ServiceUnavailable -> ErrorType.Network.ServerError
    NetworkErrorType.Network.GatewayTimeout -> ErrorType.Network.ServerError
}
