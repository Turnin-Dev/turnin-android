package com.peekr.core.data.source.network.retrofit

import com.peekr.core.common.logger.AppLogger
import com.peekr.core.data.source.local.datastore.DataStoreKey
import com.peekr.core.data.source.local.datastore.DataStoreManager
import com.peekr.core.data.source.network.retrofit.RetrofitConstants.AUTHENTICATION
import com.peekr.core.data.source.network.retrofit.RetrofitConstants.BEARER
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

/**
 * HTTP 요청 시 자동으로 헤더에 토큰을 첨부해주는 토큰 인터셉터
 *
 * 만약, 토큰이 빈 문자열이거나 null이면 원본 요청으로 계속 진행한다.
 */
class TokenInterceptor(private val dataStoreManager: DataStoreManager) : Interceptor {
    private val tag = this::class.java.simpleName

    override fun intercept(chain: Interceptor.Chain): Response {
        AppLogger.d(tag, "TokenInterceptor Triggered!")

        // get access-token & just continue request when access-token is null
        val accessToken = runBlocking {
            dataStoreManager
                .getEncryptedStringData(DataStoreKey.Auth.AccessToken)
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
            val req = response.request
            AppLogger.w(
                tag = tag,
                message = "HTTP ${response.code}: ${req.method} ${req.url} | message=${response.message}",
            )
        }

        return response
    }

    private fun loggingResponseCode(code: Int, isSuccess: Boolean) {
        when (code) {
            in 200..299 -> AppLogger.d(tag, "Response Success ($code)")
            in 400..499 -> AppLogger.w(tag, "Response Client Error ($code)")
            in 500..599 -> AppLogger.e(tag, "Response Server Error ($code)")
            else -> if (isSuccess) {
                AppLogger.d(tag, "Response Success ($code)")
            } else {
                AppLogger.w(tag, "Response Failure ($code)")
            }
        }
    }
}
