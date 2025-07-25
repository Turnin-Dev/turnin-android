package com.peekr.data.shared.retrofit

import com.peekr.data.shared.retrofit.RetrofitConstants.AUTHENTICATION
import com.peekr.data.shared.retrofit.RetrofitConstants.BEARER
import com.peekr.domain.shared.dataStore.DataStoreKey
import com.peekr.domain.shared.dataStore.DataStoreManager
import javax.inject.Inject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

/**
 * HTTP 요청 시 자동으로 헤더에 토큰을 첨부해주는 토큰 인터셉터
 *
 * 만약, 토큰이 빈 문자열이거나 null이면 원본 요청으로 계속 진행한다.
 */
class TokenInterceptor @Inject constructor(private val dataStoreManager: DataStoreManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        Timber.d("TokenInterceptor Triggered!")

        // get access-token & just continue request when access-token is null
        val accessToken = runBlocking {
            dataStoreManager
                .getEncryptedStringData(DataStoreKey.Auth.AccessToken)
                .catch { emit(null) }
                .first()
        } ?: return chain.proceed(chain.request())

        // just continue request when access-token is empty
        if (accessToken.isEmpty()) {
            return chain.proceed(chain.request())
        }

        // access-token is not null & not empty
        val requestBuilder = chain.request().newBuilder()
        requestBuilder.addHeader(AUTHENTICATION, "$BEARER $accessToken")

        val response = chain.proceed(requestBuilder.build())

        // The logic located below runs after two situations:
        // 1. after process(OkHttp's Authentication function) for HTTP Status 401
        // 2. just receive response to a request with token
        if (response.isSuccessful) {
            loggingResponseCode(response.code, true)
        } else { // Failure (Ex. 4xx, 5xx)
            loggingResponseCode(response.code, false)
            Timber.d("request: ${response.request}\n" + "message: ${response.message}")
        }

        return response
    }
}

private fun loggingResponseCode(code: Int, isSuccess: Boolean) {
    when (code) {
        200 -> {
            Timber.d("Response is Successful (HTTP status code is 200 OK)")
        }

        201 -> {
            Timber.d("Response is Successful (HTTP status code is 201 Created)")
        }

        404 -> {
            Timber.d("Response is Failure (HTTP status code is 404 Not Found)")
        }

        else -> {
            if (isSuccess) {
                Timber.d("Response is Successful (HTTP status code is $code)")
            } else {
                Timber.d("Response is Failure (HTTP status code is $code)")
            }
        }
    }
}
