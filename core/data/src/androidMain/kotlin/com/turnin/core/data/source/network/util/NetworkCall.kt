package com.turnin.core.data.source.network.util

import android.util.MalformedJsonException
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import com.turnin.core.common.logger.AppLogger
import com.turnin.core.data.source.network.error.NetworkErrorType
import com.turnin.core.data.source.network.policy.NetworkPolicy
import com.turnin.core.data.source.network.policy.isConnectionFailure
import com.turnin.core.data.source.network.policy.isTimeoutException
import java.io.IOException
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import retrofit2.Response

/**
 * 모든 네트워크 호출에 사용 되는 제네릭 함수
 *
 * @param call suspend function 이어야 하며, 반환 값은 Response 타입
 * @return [NetworkResult]
 * @see NetworkErrorType
 */
suspend fun <T> networkCall(call: suspend () -> Response<T>): NetworkResult<T> =
    executeNetworkCall(call) { response -> handleResponse(response) }

/**
 * 모든 네트워크 호출에 사용 되는 제네릭 함수
 *
 * [networkCall]과의 차이는 [networkCallWithoutResponse]는 응답 바디가 없는 요청에 대해 사용한다.
 *
 * @param call suspend function 이어야 하며, 반환 값은 Response 타입
 * @return [NetworkResult]
 * @see NetworkErrorType
 */
suspend fun <T> networkCallWithoutResponse(call: suspend () -> Response<T>): NetworkResult<Unit> =
    executeNetworkCall(call) { response -> handleResponseWithoutBody(response) }

/**
 * 공통 네트워크 호출 로직
 *
 * 예외 분류(타임아웃·연결 실패·IO)와 에러 생성은 Ktor `ktorNetworkCall`과 같은 기준을 사용한다.
 *
 * @param call suspend function 이어야 하며, 반환 값은 Response 타입
 * @param handle [Response]응답을 처리하고 [T]타입에서 -> [R]타입으로 반환한다.
 * @return [NetworkResult]
 * @see NetworkErrorType
 */
private suspend fun <T, R> executeNetworkCall(
    call: suspend () -> Response<T>,
    handle: (Response<T>) -> NetworkResult<R>,
): NetworkResult<R> = try {
    val response = retry { call().throwIfRetryableError() }
    handle(response)
} catch (e: CancellationException) {
    throw e
} catch (e: HttpException) {
    httpErrorResult(e.code(), e.response()?.errorBody()?.readTextOrNull())
} catch (e: JsonDataException) {
    handleException(NetworkErrorType.Exception.JsonData, e)
} catch (e: JsonEncodingException) {
    handleException(NetworkErrorType.Exception.JsonEncoding, e)
} catch (e: MalformedJsonException) {
    handleException(NetworkErrorType.Exception.MalformedJson, e)
} catch (e: Exception) {
    when {
        e.isTimeoutException() -> handleException(NetworkErrorType.Exception.TimeOut, e)
        e.isConnectionFailure() -> handleException(NetworkErrorType.Network.ConnectionFailed, e)
        e is IOException -> handleException(NetworkErrorType.Exception.IO, e)
        else -> handleException(NetworkErrorType.Unexpected(e), e)
    }
}

/** 응답 처리 */
private fun <T> handleResponse(response: Response<T>): NetworkResult<T> {
    val body = response.body()
    return if (response.isSuccessful && body != null) {
        NetworkResult.Success(body)
    } else {
        httpErrorResult(response.code(), response.errorBody()?.readTextOrNull())
    }
}

/** 본문이 없는 응답 처리 */
private fun <T> handleResponseWithoutBody(response: Response<T>): NetworkResult<Unit> =
    if (response.isSuccessful) {
        NetworkResult.Success(Unit)
    } else {
        httpErrorResult(response.code(), response.errorBody()?.readTextOrNull())
    }

/** Exception 처리 (로그에는 원본 메시지, 결과에는 IP 마스킹된 메시지) */
private fun handleException(errorType: NetworkErrorType, e: Throwable): NetworkResult.Error {
    AppLogger.e("NetworkCall", e, "Network exception occurred: ${e.message}")
    return exceptionErrorResult(errorType, e)
}

private fun okhttp3.ResponseBody.readTextOrNull(): String? = runCatching { string() }.getOrNull()

/** 재시도 가능한 상태 코드면 예외를 발생시켜 [retry]가 재시도하도록 한다. */
private fun <T> Response<T>.throwIfRetryableError(): Response<T> {
    if (!isSuccessful && NetworkPolicy.Retry.isRetryableStatus(code())) {
        throw HttpException(this)
    }
    return this
}
