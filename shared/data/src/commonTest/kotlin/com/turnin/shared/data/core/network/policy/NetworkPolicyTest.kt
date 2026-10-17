package com.turnin.shared.data.core.network.policy

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NetworkPolicyTest {
    @Test
    fun `Full Jitter 지연은 재시도 순번마다 상한의 절반 이상 상한 미만이다`() {
        val random = Random(seed = 42)
        val expectedRanges = listOf(250L until 500L, 500L until 1_000L, 1_000L until 2_000L, 1_250L until 2_500L)

        expectedRanges.forEachIndexed { retryIndex, range ->
            repeat(100) {
                val delay = NetworkPolicy.Retry.fullJitterDelayMillis(retryIndex = retryIndex, random = random)
                assertTrue(delay in range, "retryIndex=$retryIndex, delay=$delay")
            }
        }
    }

    @Test
    fun `서버 오류 상태 코드만 재시도한다`() {
        listOf(500, 502, 503, 504).forEach { assertTrue(NetworkPolicy.Retry.isRetryableStatus(it), "$it") }
        listOf(400, 401, 403, 404, 409, 422, 501).forEach { assertFalse(NetworkPolicy.Retry.isRetryableStatus(it), "$it") }
    }
}
