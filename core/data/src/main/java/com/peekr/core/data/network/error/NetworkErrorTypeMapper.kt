package com.peekr.core.data.network.error

import com.peekr.core.domain.common.CommonErrorType

fun NetworkErrorType.toCommonErrorType(): CommonErrorType = when (this) {
    // ------------------------------ Exception ------------------------------
    NetworkErrorType.Exception.IO -> CommonErrorType.Exception.IO
    NetworkErrorType.Exception.TimeOut -> CommonErrorType.Exception.TimeOut
    NetworkErrorType.Exception.JsonData -> CommonErrorType.Exception.Json
    NetworkErrorType.Exception.JsonEncoding -> CommonErrorType.Exception.Json
    NetworkErrorType.Exception.MalformedJson -> CommonErrorType.Exception.Json
    // ------------------------------ Network ------------------------------
    NetworkErrorType.Network.Unauthorized -> CommonErrorType.Network.Unauthorized
    NetworkErrorType.Network.EmptyResponse -> CommonErrorType.Network.ClientError
    NetworkErrorType.Network.BadRequest -> CommonErrorType.Network.ClientError
    NetworkErrorType.Network.Forbidden -> CommonErrorType.Network.ClientError
    NetworkErrorType.Network.NotFound -> CommonErrorType.Network.ClientError
    NetworkErrorType.Network.RequestTimeout -> CommonErrorType.Network.ClientError
    NetworkErrorType.Network.HttpError -> CommonErrorType.Network.ClientError
    NetworkErrorType.Network.Conflict -> CommonErrorType.Network.ClientError
    NetworkErrorType.Network.InternalServerError -> CommonErrorType.Network.ServerError
    NetworkErrorType.Network.BadGateway -> CommonErrorType.Network.ServerError
    NetworkErrorType.Network.ServiceUnavailable -> CommonErrorType.Network.ServerError
    NetworkErrorType.Network.GatewayTimeout -> CommonErrorType.Network.ServerError
    NetworkErrorType.Network.ConnectionFailed -> CommonErrorType.Network.ConnectionFailed
    NetworkErrorType.Network.InvalidFileType -> CommonErrorType.Network.InvalidFileType
    NetworkErrorType.Network.UploadFileFailed -> CommonErrorType.Network.UploadFileFailed
    // ------------------------------ Unexpected ------------------------------
    is NetworkErrorType.Unexpected -> CommonErrorType.Unexpected(this.cause)
}
