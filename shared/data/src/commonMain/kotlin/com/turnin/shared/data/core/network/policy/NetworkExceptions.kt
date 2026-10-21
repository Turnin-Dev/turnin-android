package com.turnin.shared.data.core.network.policy

import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException

// Retrofit(`:core:data`)·Ktor 네트워크 호출이 같은 기준으로 예외를 분류하도록 공개한다.

/** 타임아웃 예외인지 확인 (Ktor 타임아웃 + 플랫폼 타임아웃) */
fun Throwable.isTimeoutException(): Boolean =
    this is HttpRequestTimeoutException ||
        this is ConnectTimeoutException ||
        this is SocketTimeoutException ||
        isPlatformTimeoutException()

/** 플랫폼 타임아웃 예외인지 확인 */
expect fun Throwable.isPlatformTimeoutException(): Boolean

/** 호스트 조회(DNS) 실패 예외인지 확인 */
expect fun Throwable.isUnknownHostException(): Boolean

/** 서버 연결 실패 예외인지 확인 */
expect fun Throwable.isConnectionFailure(): Boolean
