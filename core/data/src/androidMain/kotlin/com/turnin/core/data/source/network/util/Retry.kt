package com.turnin.core.data.source.network.util

import com.turnin.core.common.logger.AppLogger
import com.turnin.core.data.source.network.policy.NetworkPolicy
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import retrofit2.HttpException

/**
 * Retry Function, Exponential Backoff With Full Jitter
 *
 * 재시도 판정·지연 계산은 Ktor와 같은 [NetworkPolicy.Retry]를 사용하며, **모든 재시도 전에 대기**한다.
 * 총 시도 횟수는 ([attempt] + 1)번 이다.
 *
 * @param attempt 최대 재시도 횟수
 * @param initialDelayMillis 초기 딜레이
 * @param maxDelayMillis 최대 딜레이
 * @param factor factor 밑 (지수는 재시도 순번)
 * @param block suspend function
 */
suspend fun <T> retry(
    attempt: Int = NetworkPolicy.Retry.MAX_RETRIES,
    initialDelayMillis: Long = NetworkPolicy.Retry.INITIAL_DELAY_MILLIS,
    maxDelayMillis: Long = NetworkPolicy.Retry.MAX_DELAY_MILLIS,
    factor: Double = NetworkPolicy.Retry.FACTOR,
    block: suspend () -> T,
): T {
    require(attempt >= 0) { "attempt must be positive." }

    var retryIndex = 0
    while (true) {
        try {
            return block()
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            // 재시도 불가능한 예외(인증 관련 상태 코드 등)는 즉시 실패 처리
            if (!e.isRetryable()) {
                AppLogger.w(TAG, "Non-retryable exception: ${e.localizedMessage}")
                throw e
            }

            // 재시도 횟수 소진
            if (retryIndex >= attempt) throw e

            val delayMillis = NetworkPolicy.Retry.fullJitterDelayMillis(
                retryIndex = retryIndex,
                initialDelayMillis = initialDelayMillis,
                maxDelayMillis = maxDelayMillis,
                factor = factor,
            )
            AppLogger.d(TAG, "retry #${retryIndex + 1} after $delayMillis ms")
            delay(delayMillis)
            retryIndex++
        }
    }
}

/** 재시도 가능한 예외인지 확인 */
private fun Exception.isRetryable(): Boolean = when (this) {
    is HttpException -> NetworkPolicy.Retry.isRetryableStatus(code())
    else -> NetworkPolicy.Retry.isRetryableException(this)
}

private const val TAG = "Retry"
