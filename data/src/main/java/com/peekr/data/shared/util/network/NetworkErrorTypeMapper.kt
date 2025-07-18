package com.peekr.data.shared.util.network

import com.peekr.domain.shared.util.ErrorType

/**
 * NetworkErrorType 값을 해당하는 ErrorType 값으로 변환합니다.
 *
 * 데이터 계층의 네트워크 오류 타입을 도메인 계층의 오류 타입으로 매핑할 때 사용됩니다.
 *
 * @return 매핑된 ErrorType 값
 */
fun NetworkErrorType.toErrorType(): ErrorType = when (this) {
    // ------------------------------ Exception ------------------------------
    NetworkErrorType.Exception.IO -> ErrorType.Exception.IO
    NetworkErrorType.Exception.TimeOut -> ErrorType.Exception.TimeOut
    NetworkErrorType.Exception.JsonData -> ErrorType.Exception.Json
    NetworkErrorType.Exception.JsonEncoding -> ErrorType.Exception.Json
    NetworkErrorType.Exception.MalformedJson -> ErrorType.Exception.Json
    NetworkErrorType.Exception.Unexpected -> ErrorType.Exception.Unexpected
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
    NetworkErrorType.Network.Conflict -> ErrorType.Network.Conflict
    NetworkErrorType.Network.GatewayTimeout -> ErrorType.Network.GatewayTimeout
}
