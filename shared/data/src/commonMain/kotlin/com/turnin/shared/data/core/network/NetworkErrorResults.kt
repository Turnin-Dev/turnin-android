package com.turnin.shared.data.core.network

import com.turnin.shared.domain.core.error.NetworkErrorType
import kotlinx.serialization.json.Json

/** 네트워크 응답 파싱에 사용하는 Json 설정 */
internal val networkJson = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
}

private val ipRegex = Regex("""\d{1,3}(\.\d{1,3}){3}""")

/**
 * HTTP 에러 응답을 [NetworkResult.Error]로 변환한다.
 *
 * @param status HTTP 상태 코드
 * @param errorBody 에러 응답 바디 ([CommonErrorResponse] 형식이 아니면 code·message 없이 반환)
 */
internal fun httpErrorResult(status: Int, errorBody: String?): NetworkResult.Error {
    val errorResponse = errorBody?.let {
        runCatching { networkJson.decodeFromString<CommonErrorResponse>(it) }.getOrNull()
    }
    return NetworkResult.Error(
        error = status.toNetworkErrorType(),
        code = errorResponse?.code,
        status = status,
        message = errorResponse?.message,
    )
}

/**
 * 예외를 [NetworkResult.Error]로 변환한다.
 *
 * 메시지에 포함된 IPv4 주소는 마스킹한다.
 */
internal fun exceptionErrorResult(errorType: NetworkErrorType, throwable: Throwable): NetworkResult.Error =
    NetworkResult.Error(
        error = errorType,
        message = throwable.message?.replace(ipRegex, "XXX.XXX.XXX.XXX"),
    )

/** HTTP 에러 상태 코드를 [NetworkErrorType]으로 변환한다. */
internal fun Int.toNetworkErrorType(): NetworkErrorType = when (this) {
    HttpStatusCode.BadRequest.code -> NetworkErrorType.Network.BadRequest
    HttpStatusCode.Unauthorized.code -> NetworkErrorType.Network.Unauthorized
    HttpStatusCode.Forbidden.code -> NetworkErrorType.Network.Forbidden
    HttpStatusCode.NotFound.code -> NetworkErrorType.Network.NotFound
    HttpStatusCode.Conflict.code -> NetworkErrorType.Network.Conflict
    HttpStatusCode.RequestTimeout.code -> NetworkErrorType.Network.RequestTimeout
    in 400..499 -> NetworkErrorType.Network.ClientError(this)
    HttpStatusCode.InternalServerError.code -> NetworkErrorType.Network.InternalServerError
    HttpStatusCode.BadGateway.code -> NetworkErrorType.Network.BadGateway
    HttpStatusCode.ServiceUnavailable.code -> NetworkErrorType.Network.ServiceUnavailable
    HttpStatusCode.GatewayTimeout.code -> NetworkErrorType.Network.GatewayTimeout
    in 500..599 -> NetworkErrorType.Network.ServerError(this)
    else -> NetworkErrorType.Unexpected(null)
}
