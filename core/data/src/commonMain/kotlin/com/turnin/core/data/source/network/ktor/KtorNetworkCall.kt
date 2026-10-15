package com.turnin.core.data.source.network.ktor

import com.turnin.core.data.source.network.error.NetworkErrorType
import com.turnin.core.data.source.network.policy.isConnectionFailure
import com.turnin.core.data.source.network.policy.isTimeoutException
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.data.source.network.util.exceptionErrorResult
import com.turnin.core.data.source.network.util.httpErrorResult
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import io.ktor.serialization.ContentConvertException
import kotlinx.coroutines.CancellationException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

/**
 * Ktor 네트워크 호출 공통 함수 (Retrofit `networkCall`의 Ktor 버전)
 *
 * @param call [HttpResponse]를 반환하는 요청
 * @return [NetworkResult]
 * @see NetworkErrorType
 */
suspend inline fun <reified T> ktorNetworkCall(noinline call: suspend () -> HttpResponse): NetworkResult<T> =
    executeKtorCall(call) { response -> response.body<T>() }

/**
 * 응답 바디가 없는 요청에 사용하는 Ktor 네트워크 호출 함수
 *
 * @param call [HttpResponse]를 반환하는 요청
 * @return [NetworkResult]
 */
suspend fun ktorNetworkCallWithoutResponse(call: suspend () -> HttpResponse): NetworkResult<Unit> =
    executeKtorCall(call) { }

/**
 * 공통 Ktor 호출 로직
 *
 * 2xx는 [parse] 결과를 성공으로, 그 외 상태 코드는 서버 에러 응답을 파싱해 [NetworkErrorType.Network.HttpError]로 반환한다.
 * 예외 분류는 Retrofit `networkCall`과 같은 기준(`NetworkExceptions`)을 사용한다.
 */
@PublishedApi
internal suspend fun <T> executeKtorCall(
    call: suspend () -> HttpResponse,
    parse: suspend (HttpResponse) -> T,
): NetworkResult<T> = try {
    val response = call()
    if (response.status.isSuccess()) {
        NetworkResult.Success(parse(response))
    } else {
        httpErrorResult(response.status.value, runCatching { response.bodyAsText() }.getOrNull())
    }
} catch (e: CancellationException) {
    throw e
} catch (e: SerializationException) {
    exceptionErrorResult(NetworkErrorType.Exception.JsonData, e)
} catch (e: ContentConvertException) {
    exceptionErrorResult(NetworkErrorType.Exception.JsonData, e)
} catch (e: Exception) {
    when {
        e.isTimeoutException() -> exceptionErrorResult(NetworkErrorType.Exception.TimeOut, e)
        e.isConnectionFailure() -> exceptionErrorResult(NetworkErrorType.Network.ConnectionFailed, e)
        e is IOException -> exceptionErrorResult(NetworkErrorType.Exception.IO, e)
        else -> exceptionErrorResult(NetworkErrorType.Unexpected(e), e)
    }
}
