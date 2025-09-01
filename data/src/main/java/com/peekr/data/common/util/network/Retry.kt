package com.peekr.data.common.util.network

import com.peekr.data.common.util.network.NetworkRetryPolicy.NON_RETRYABLE_STATUS_CODES
import com.peekr.data.common.util.network.NetworkRetryPolicy.RETRYABLE_STATUS_CODES
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException
import kotlin.math.pow
import kotlin.random.Random
import kotlinx.coroutines.delay
import retrofit2.HttpException
import timber.log.Timber

/**
 * Retry Function, Exponential Backoff With Jitter
 *
 * 재시도 횟수는 ([attempt] + 1(마지막 시도))번 이다.
 *
 * @param attempt 재시도 횟수
 * @param initialDelayMillis 초기 딜레이
 * @param maxDelayMillis 최대 딜레이
 * @param factor factor 밑 (지수는 attempt - 1)
 * @param block suspend function
 */
suspend fun <T> retry(
    attempt: Int = 2,
    initialDelayMillis: Long = 500,
    maxDelayMillis: Long = 2500,
    factor: Double = 2.0,
    block: suspend () -> T,
): T {
    require(attempt >= 0) { "attempt must be positive." }

    repeat(attempt) {
        try {
            return block()
        } catch (e: Exception) {
            // 인증 관련 요청인 경우 즉시 실패 처리
            if (e is HttpException && e.code() in NON_RETRYABLE_STATUS_CODES) {
                Timber.w("Authentication failed with status ${e.code()}, not retrying")
                throw e
            }

            // 재시도 불가능한 예외 체크
            if (!isRetryableException(e)) {
                Timber.w("Non-retryable exception: ${e.localizedMessage}")
                throw e
            }

            // 재시도
            val isLastAttempt = it < attempt - 1
            if (isLastAttempt) {
                val fullJitterDelay =
                    calculateFullJitterDelay(it, initialDelayMillis, maxDelayMillis, factor)
                Timber.d("retry delay: $fullJitterDelay ms")
                delay(fullJitterDelay)
            }
        }
    }

    // 마지막 시도 부분
    // 만약, attempt 가 2인 경우
    // 총 재시도 횟수는 3이다. (attempt + 마지막 시도)
    return block()
}

/** FullJitterDelay 계산하는 함수 */
private fun calculateFullJitterDelay(
    attempt: Int,
    initialDelayMillis: Long,
    maxDelayMillis: Long,
    factor: Double,
): Long {
    val temp =
        maxDelayMillis.coerceAtMost(initialDelayMillis * factor.pow(attempt).toLong())
    val fullJitterDelay = (temp / 2) + Random.nextLong(0, temp / 2)
    Timber.d("Network call retry delay: $fullJitterDelay")
    return fullJitterDelay
}

/** 재시도 가능한 예외인지 확인 */
private fun isRetryableException(exception: Exception): Boolean =
    when (exception) {
        is HttpException -> exception.code() in RETRYABLE_STATUS_CODES
        is SocketTimeoutException,
        is UnknownHostException,
        is TimeoutException,
        -> true

        else -> false
    }
