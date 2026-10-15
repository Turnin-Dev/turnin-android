package com.turnin.core.data.source.network.policy

import io.ktor.client.engine.darwin.DarwinHttpRequestException
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import platform.Foundation.NSError
import platform.Foundation.NSURLErrorCannotConnectToHost
import platform.Foundation.NSURLErrorCannotFindHost
import platform.Foundation.NSURLErrorDomain
import platform.Foundation.NSURLErrorNotConnectedToInternet
import platform.Foundation.NSURLErrorTimedOut

class NetworkExceptionsIosTest {
    private fun darwinError(code: Long) =
        DarwinHttpRequestException(NSError.errorWithDomain(NSURLErrorDomain, code, null))

    @Test
    fun `NSURLError 코드를 Android와 같은 기준으로 분류한다`() {
        assertTrue(darwinError(NSURLErrorTimedOut).isTimeoutException())
        assertTrue(darwinError(NSURLErrorCannotFindHost).isUnknownHostException())
        assertTrue(darwinError(NSURLErrorCannotConnectToHost).isConnectionFailure())

        // 그 외 네트워크 에러는 IO로 처리된다
        val notConnected = darwinError(NSURLErrorNotConnectedToInternet)
        assertFalse(notConnected.isTimeoutException())
        assertFalse(notConnected.isUnknownHostException())
        assertFalse(notConnected.isConnectionFailure())
    }

    @Test
    fun `재시도 정책은 타임아웃과 호스트 조회 실패만 재시도한다`() {
        assertTrue(NetworkPolicy.Retry.isRetryableException(darwinError(NSURLErrorTimedOut)))
        assertTrue(NetworkPolicy.Retry.isRetryableException(darwinError(NSURLErrorCannotFindHost)))
        assertFalse(NetworkPolicy.Retry.isRetryableException(darwinError(NSURLErrorCannotConnectToHost)))
    }
}
