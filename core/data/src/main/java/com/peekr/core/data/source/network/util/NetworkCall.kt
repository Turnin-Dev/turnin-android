package com.peekr.core.data.source.network.util

import android.util.MalformedJsonException
import com.peekr.core.common.logger.AppLogger
import com.peekr.core.data.source.network.error.CommonErrorResponse
import com.peekr.core.data.source.network.error.NetworkErrorType
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import retrofit2.Response

private val moshi: Moshi by lazy {
    Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
}

private val moshiAdapter by lazy {
    moshi.adapter(CommonErrorResponse::class.java)
}

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
} catch (e: HttpException) {
    handleHttpException(e)
} catch (e: ConnectException) {
    handleException(NetworkErrorType.Network.ConnectionFailed, e)
} catch (e: SocketTimeoutException) {
    handleException(NetworkErrorType.Exception.TimeOut, e)
} catch (e: JsonDataException) {
    handleException(NetworkErrorType.Exception.JsonData, e)
} catch (e: JsonEncodingException) {
    handleException(NetworkErrorType.Exception.JsonEncoding, e)
} catch (e: MalformedJsonException) {
    handleException(NetworkErrorType.Exception.MalformedJson, e)
} catch (e: IOException) {
    handleException(NetworkErrorType.Exception.IO, e)
} catch (e: TimeoutException) {
    handleException(NetworkErrorType.Exception.TimeOut, e)
} catch (e: Exception) {
    if (e is CancellationException) throw e
    handleException(NetworkErrorType.Unexpected(e), e)
}

/** 응답 처리 */
private fun <T> handleResponse(response: Response<T>): NetworkResult<T> = when {
    response.isSuccessful && response.body() != null -> {
        NetworkResult.Success(response.body()!!)
    }

    else -> {
        val errorResponse = parseServerError(response)
        NetworkResult.Error(
            error = NetworkErrorType.Network.HttpError(response.code()),
            code = errorResponse?.code,
            status = response.code(),
            message = errorResponse?.message,
        )
    }
}

/** 본문이 없는 응답 처리 */
private fun <T> handleResponseWithoutBody(response: Response<T>): NetworkResult<Unit> =
    if (response.isSuccessful) {
        NetworkResult.Success(Unit)
    } else {
        val errorResponse = parseServerError(response)
        NetworkResult.Error(
            error = NetworkErrorType.Network.HttpError(response.code()),
            code = errorResponse?.code,
            status = response.code(),
            message = errorResponse?.message,
        )
    }

/** HTTP Exception 처리 */
private fun handleHttpException(e: HttpException): NetworkResult.Error {
    val errorResponse = parseServerErrorFromException(e)
    return NetworkResult.Error(
        error = NetworkErrorType.Network.HttpError(e.code()),
        code = errorResponse?.code,
        status = e.code(),
        message = errorResponse?.message,
    )
}

/** Exception 처리 */
private fun handleException(errorType: NetworkErrorType, e: Throwable): NetworkResult.Error {
    // 1. 로그에는 원본 메시지 전체 출력 (개발 확인용)
    AppLogger.e("NetworkCall", e, "Network exception occurred: ${e.message}")

    // 2. IP 주소 패턴 (IPv4) 매칭 및 마스킹
    val ipRegex = Regex("""\d{1,3}(\.\d{1,3}){3}""")
    val maskedMessage = e.message?.replace(ipRegex, "XXX.XXX.XXX.XXX")

    // 3. 마스킹된 메시지 반환
    return NetworkResult.Error(error = errorType, message = maskedMessage)
}

/** HttpException에서 서버 에러 파싱 */
private fun parseServerErrorFromException(e: HttpException): CommonErrorResponse? = runCatching {
    e
        .response()
        ?.errorBody()
        ?.source()
        ?.let { source -> moshiAdapter.fromJson(source) }
}.getOrNull()

/** 서버 에러 응답 파싱 */
private fun <T> parseServerError(response: Response<T>): CommonErrorResponse? = runCatching {
    response.errorBody()?.source()?.let { source -> moshiAdapter.fromJson(source) }
}.getOrNull()

/** 상태에 맞는 예외 발생 함수 */
private fun <T> Response<T>.throwIfRetryableError(): Response<T> {
    if (!isSuccessful && code() in NetworkRetryPolicy.RETRYABLE_STATUS_CODES) {
        throw HttpException(this)
    }
    return this
}
