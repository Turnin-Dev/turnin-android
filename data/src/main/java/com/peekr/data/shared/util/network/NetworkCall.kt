package com.peekr.data.shared.util.network

import android.util.MalformedJsonException
import com.peekr.data.shared.util.NetworkResult
import com.peekr.data.shared.util.error.CommonErrorResponse
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException
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
 * 네트워크 호출을 실행하고 결과를 처리한다.
 *
 * suspend 함수 형태의 네트워크 요청을 실행하며, 응답을 지정된 핸들러로 가공하여 `NetworkResult`로 반환한다.
 * 네트워크 오류, HTTP 예외, JSON 파싱 오류 등 다양한 예외 상황을 적절한 에러 타입으로 변환한다.
 *
 * @param call suspend 함수로, Retrofit의 `Response<T>`를 반환해야 한다.
 * @param handle 네트워크 응답을 받아 `NetworkResult<R>`로 변환하는 처리 함수.
 * @return 네트워크 호출 결과를 나타내는 `NetworkResult<R>`.
 */
private suspend fun <T, R> executeNetworkCall(
    call: suspend () -> Response<T>,
    handle: (Response<T>) -> NetworkResult<R>,
): NetworkResult<R> = try {
    val response = retry { call().throwIfRetryableError() }
    handle(response)
} catch (e: HttpException) {
    handleHttpException(e)
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
    handleException(NetworkErrorType.Exception.Unexpected, e)
}

/** 응답 처리 */
private fun <T> handleResponse(response: Response<T>): NetworkResult<T> = when {
    response.isSuccessful && response.body() != null -> {
        NetworkResult.Success(response.body()!!)
    }

    response.isSuccessful && response.body() == null -> {
        NetworkResult.Error(
            error = NetworkErrorType.Network.EmptyResponse,
            status = response.code(),
        )
    }

    else -> {
        val errorResponse = parseServerError(response)
        NetworkResult.Error(
            error = mapHttpStatusToErrorType(response.code()),
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
            error = mapHttpStatusToErrorType(response.code()),
            code = errorResponse?.code,
            status = response.code(),
            message = errorResponse?.message,
        )
    }

/** HTTP Exception 처리 */
private fun handleHttpException(e: HttpException): NetworkResult.Error {
    val errorResponse = parseServerErrorFromException(e)
    return NetworkResult.Error(
        error = mapHttpStatusToErrorType(e.code()),
        code = errorResponse?.code,
        status = e.code(),
        message = errorResponse?.message,
    )
}

/** Exception 처리 */
private fun handleException(errorType: NetworkErrorType, e: Throwable): NetworkResult.Error =
    NetworkResult.Error(error = errorType, message = e.message)

/** HttpException에서 서버 에러 파싱 */
private fun parseServerErrorFromException(e: HttpException): CommonErrorResponse? = runCatching {
    e
        .response()
        ?.errorBody()
        ?.source()
        ?.let { source -> moshiAdapter.fromJson(source) }
}.getOrNull()

/**
 * HTTP 상태 코드를 해당하는 네트워크 에러 타입으로 매핑합니다.
 *
 * @param statusCode HTTP 응답 상태 코드
 * @return 상태 코드에 대응하는 [NetworkErrorType.Network] 값
 */
private fun mapHttpStatusToErrorType(statusCode: Int): NetworkErrorType = when (statusCode) {
    400 -> NetworkErrorType.Network.BadRequest
    401 -> NetworkErrorType.Network.Unauthorized
    403 -> NetworkErrorType.Network.Forbidden
    404 -> NetworkErrorType.Network.NotFound
    408 -> NetworkErrorType.Network.RequestTimeout
    409 -> NetworkErrorType.Network.Conflict
    500 -> NetworkErrorType.Network.InternalServerError
    502 -> NetworkErrorType.Network.BadGateway
    503 -> NetworkErrorType.Network.ServiceUnavailable
    504 -> NetworkErrorType.Network.GatewayTimeout
    else -> NetworkErrorType.Network.HttpError
}

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
