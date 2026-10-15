package com.turnin.core.data.source.network.policy

import com.turnin.core.data.HttpStatusCode
import kotlin.math.pow
import kotlin.random.Random

/**
 * Retrofit·Ktor 네트워크 호출이 공유하는 정책 (재시도·타임아웃)
 *
 * 두 네트워크 스택의 동작이 어긋나지 않도록 값과 판정 로직은 이 객체에서만 정의한다.
 */
object NetworkPolicy {
    /**
     * 재시도 정책: 지수 백오프 + Full Jitter, **모든 재시도 전에 대기**한다.
     *
     * 총 시도 횟수는 [MAX_RETRIES] + 1 이다.
     */
    object Retry {
        /** 최대 재시도 횟수 */
        const val MAX_RETRIES = 2

        /** 첫 재시도 지연 기준값 */
        const val INITIAL_DELAY_MILLIS = 500L

        /** 지연 상한 */
        const val MAX_DELAY_MILLIS = 2_500L

        /** 지수 증가 밑 */
        const val FACTOR = 2.0

        /** 재시도 가능한 상태 코드 (서버 관련). 그 외 상태 코드(인증 관련 4xx 등)는 즉시 실패 처리한다. */
        val RetryableStatusCodes: Set<Int> = setOf(
            HttpStatusCode.InternalServerError.code,
            HttpStatusCode.BadGateway.code,
            HttpStatusCode.ServiceUnavailable.code,
            HttpStatusCode.GatewayTimeout.code,
        )

        /** 재시도 가능한 상태 코드인지 확인 */
        fun isRetryableStatus(status: Int): Boolean = status in RetryableStatusCodes

        /** 재시도 가능한 예외인지 확인 (타임아웃, 호스트 조회 실패) */
        fun isRetryableException(throwable: Throwable): Boolean =
            throwable.isTimeoutException() || throwable.isUnknownHostException()

        /**
         * Full Jitter 지연 시간을 계산한다.
         *
         * `min(maxDelay, initialDelay * factor^retryIndex)`의 절반 ~ 전체 사이 값
         *
         * @param retryIndex 0부터 시작하는 재시도 순번
         */
        fun fullJitterDelayMillis(
            retryIndex: Int,
            initialDelayMillis: Long = INITIAL_DELAY_MILLIS,
            maxDelayMillis: Long = MAX_DELAY_MILLIS,
            factor: Double = FACTOR,
            random: Random = Random,
        ): Long {
            val capped = maxDelayMillis.coerceAtMost((initialDelayMillis * factor.pow(retryIndex)).toLong())
            val half = capped / 2
            return if (half <= 0) capped else half + random.nextLong(0, half)
        }
    }

    /**
     * 타임아웃 정책
     *
     * Ktor `HttpTimeout`에는 read·write 중 큰 값을 socket 타임아웃으로, call을 request 타임아웃으로 사용한다.
     */
    data class Timeout(
        val connectMillis: Long,
        val readMillis: Long,
        val writeMillis: Long,
        val callMillis: Long,
    ) {
        companion object {
            /** 일반 API */
            val Default = Timeout(connectMillis = 5_000, readMillis = 10_000, writeMillis = 10_000, callMillis = 15_000)

            /** 파일 업로드·다운로드 */
            val File = Timeout(connectMillis = 10_000, readMillis = 30_000, writeMillis = 30_000, callMillis = 60_000)
        }
    }
}
