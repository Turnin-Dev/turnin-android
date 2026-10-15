package com.turnin.core.data.source.network.util

import com.turnin.core.data.source.network.error.CommonErrorResponse
import com.turnin.core.data.source.network.error.NetworkErrorType
import kotlinx.serialization.json.Json

/** 네트워크 응답 파싱에 사용하는 Json 설정 (Retrofit 에러 바디·Ktor 공용) */
internal val networkJson = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
}

private val ipRegex = Regex("""\d{1,3}(\.\d{1,3}){3}""")

/**
 * HTTP 에러 응답을 [NetworkResult.Error]로 변환한다. (Retrofit·Ktor 공용)
 *
 * @param status HTTP 상태 코드
 * @param errorBody 에러 응답 바디 ([CommonErrorResponse] 형식이 아니면 code·message 없이 반환)
 */
internal fun httpErrorResult(status: Int, errorBody: String?): NetworkResult.Error {
    val errorResponse = errorBody?.let {
        runCatching { networkJson.decodeFromString<CommonErrorResponse>(it) }.getOrNull()
    }
    return NetworkResult.Error(
        error = NetworkErrorType.Network.HttpError(status),
        code = errorResponse?.code,
        status = status,
        message = errorResponse?.message,
    )
}

/**
 * 예외를 [NetworkResult.Error]로 변환한다. (Retrofit·Ktor 공용)
 *
 * 메시지에 포함된 IPv4 주소는 마스킹한다.
 */
internal fun exceptionErrorResult(errorType: NetworkErrorType, throwable: Throwable): NetworkResult.Error =
    NetworkResult.Error(
        error = errorType,
        message = throwable.message?.replace(ipRegex, "XXX.XXX.XXX.XXX"),
    )
