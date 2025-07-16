package com.peekr.data.shared.util.network

import kotlin.math.pow
import kotlin.random.Random
import kotlinx.coroutines.delay
import timber.log.Timber

// TODO 매개변수들 다른 서비스들 참고해서 실제 수치로 변경하기
// TODO 주의사항: 올바른 사용자 인증 정보가 제공되기 전까지는 승인되지 않는 HTTP 요청은 다시 시도해서는 안됨 (By Google)

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
    repeat(attempt) {
        val temp =
            maxDelayMillis.coerceAtMost(initialDelayMillis * factor.pow(attempt - 1).toLong())
        val fullJitterDelay = (temp / 2) + Random.nextLong(0, temp / 2)
        Timber.d("Network call retry delay: $fullJitterDelay")

        try {
            return block()
        } catch (e: Exception) {
            // logging or analysis
            Timber.e("Network call exception: ${e.localizedMessage}")
        }

        delay(fullJitterDelay)
    }

    // 마지막 시도 부분
    // 만약, attempt 가 2인 경우
    // 총 재시도 횟수는 3이다. (attempt + 마지막 시도)
    try {
        return block()
    } catch (e: Exception) {
        Timber.e("The number of retries has been exceeded: ${e.localizedMessage}")
        throw e
    }
}
