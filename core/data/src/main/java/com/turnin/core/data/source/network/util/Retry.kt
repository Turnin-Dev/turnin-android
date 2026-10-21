package com.turnin.core.data.source.network.util

import com.turnin.core.common.logger.AppLogger
import com.turnin.shared.data.core.network.policy.NetworkPolicy
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import retrofit2.HttpException

/**
 * Retry Function, Exponential Backoff With Jitter
 *
 * 재시도 정책(재시도 가능 여부·지연 시간)은 Ktor와 같은 [NetworkPolicy]를 사용한다.
 * 재시도 횟수는 ([attempt] + 1(마지막 시도))번 이다.
 *
 * @param attempt 재시도 횟수
 * @param initialDelayMillis 초기 딜레이
 * @param maxDelayMillis 최대 딜레이
 * @param factor factor 밑 (지수는 attempt - 1)
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

    repeat(attempt) {
        try {
            return block()
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            // 재시도 불가능한 예외(인증 관련 상태 코드 포함)는 즉시 실패 처리
            if (!isRetryableException(e)) {
                AppLogger.w(TAG, "Non-retryable exception: ${e.localizedMessage}")
                throw e
            }

            // 재시도 전 대기 (마지막 시도 직전 포함, 모든 재시도 전에 대기)
            val fullJitterDelay = NetworkPolicy.Retry.fullJitterDelayMillis(
                retryIndex = it,
                initialDelayMillis = initialDelayMillis,
                maxDelayMillis = maxDelayMillis,
                factor = factor,
            )
            AppLogger.d(TAG, "retry delay: $fullJitterDelay ms")
            delay(fullJitterDelay)
        }
    }

    // 마지막 시도 부분
    // 만약, attempt 가 2인 경우
    // 총 재시도 횟수는 3이다. (attempt + 마지막 시도)
    return block()
}

/** 재시도 가능한 예외인지 확인 (서버 오류 상태 코드, 타임아웃, 호스트 조회 실패) */
private fun isRetryableException(exception: Exception): Boolean =
    when (exception) {
        is HttpException -> NetworkPolicy.Retry.isRetryableStatus(exception.code())
        else -> NetworkPolicy.Retry.isRetryableException(exception)
    }

private const val TAG = "Retry"
