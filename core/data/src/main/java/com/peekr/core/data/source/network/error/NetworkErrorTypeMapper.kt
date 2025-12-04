package com.peekr.core.data.source.network.error

import com.peekr.core.data.HttpStatusCode
import com.peekr.core.domain.common.error.CommonErrorType

fun NetworkErrorType.toCommonErrorType(): CommonErrorType = when (this) {
    // ------------------------------ Exception ------------------------------
    NetworkErrorType.Exception.IO -> CommonErrorType.Exception.IO
    NetworkErrorType.Exception.TimeOut -> CommonErrorType.Exception.TimeOut
    NetworkErrorType.Exception.JsonData -> CommonErrorType.Exception.Json
    NetworkErrorType.Exception.JsonEncoding -> CommonErrorType.Exception.Json
    NetworkErrorType.Exception.MalformedJson -> CommonErrorType.Exception.Json
    // ------------------------------ Network ------------------------------
    NetworkErrorType.Network.ConnectionFailed -> CommonErrorType.Network.ConnectionFailed
    NetworkErrorType.Network.InvalidFileType -> CommonErrorType.Network.InvalidFileType
    NetworkErrorType.Network.UploadFileFailed -> CommonErrorType.Network.UploadFileFailed
    is NetworkErrorType.Network.HttpError -> {
        when (this.status) {
            HttpStatusCode.BadRequest.code -> CommonErrorType.Network.BadRequest
            HttpStatusCode.Unauthorized.code -> CommonErrorType.Network.Unauthorized
            HttpStatusCode.Forbidden.code -> CommonErrorType.Network.Forbidden
            HttpStatusCode.NotFound.code -> CommonErrorType.Network.NotFound
            HttpStatusCode.Conflict.code -> CommonErrorType.Network.Conflict
            HttpStatusCode.RequestTimeout.code -> CommonErrorType.Network.RequestTimeout
            in 400..499 -> CommonErrorType.Network.ClientError(this.status)
            HttpStatusCode.BadGateway.code -> CommonErrorType.Network.BadGateway
            HttpStatusCode.ServiceUnavailable.code -> CommonErrorType.Network.ServiceUnavailable
            HttpStatusCode.InternalServerError.code -> CommonErrorType.Network.InternalServerError
            HttpStatusCode.GatewayTimeout.code -> CommonErrorType.Network.GatewayTimeout
            in 500..599 -> CommonErrorType.Network.ServerError(this.status)
            else -> CommonErrorType.Unexpected(null)
        }
    }
    // ------------------------------ Unexpected ------------------------------
    is NetworkErrorType.Unexpected -> CommonErrorType.Unexpected(this.cause)
}
