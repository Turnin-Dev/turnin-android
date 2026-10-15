package com.turnin.core.data.source.network.policy

import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException

/** 타임아웃 예외인지 확인 (Ktor 타임아웃 + 플랫폼 타임아웃) */
internal fun Throwable.isTimeoutException(): Boolean =
    this is HttpRequestTimeoutException ||
        this is ConnectTimeoutException ||
        this is SocketTimeoutException ||
        isPlatformTimeoutException()

/** 플랫폼 타임아웃 예외인지 확인 */
internal expect fun Throwable.isPlatformTimeoutException(): Boolean

/** 호스트 조회(DNS) 실패 예외인지 확인 */
internal expect fun Throwable.isUnknownHostException(): Boolean

/** 서버 연결 실패 예외인지 확인 */
internal expect fun Throwable.isConnectionFailure(): Boolean
