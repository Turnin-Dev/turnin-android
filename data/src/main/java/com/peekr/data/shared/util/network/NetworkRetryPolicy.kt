package com.peekr.data.shared.util.network

import java.net.HttpURLConnection

/** 네트워크 재시도 정책 */
object NetworkRetryPolicy {
    /** 재시도 불가능한 상태 코드 (인증 관련) */
    val NON_RETRYABLE_STATUS_CODES = setOf(
        HttpURLConnection.HTTP_UNAUTHORIZED, // 401
        HttpURLConnection.HTTP_FORBIDDEN, // 403
        HttpURLConnection.HTTP_NOT_FOUND, // 404
        HttpURLConnection.HTTP_BAD_REQUEST, // 400
        HttpURLConnection.HTTP_CONFLICT, // 409
        422, // Unprocessable Entity
    )

    /** 재시도 가능한 상태 코드 (서버 관련) */
    val RETRYABLE_STATUS_CODES = setOf(
        HttpURLConnection.HTTP_INTERNAL_ERROR, // 500
        HttpURLConnection.HTTP_BAD_GATEWAY, // 502
        HttpURLConnection.HTTP_UNAVAILABLE, // 503
        HttpURLConnection.HTTP_GATEWAY_TIMEOUT, // 504
        429, // Too Many Requests
    )
}
