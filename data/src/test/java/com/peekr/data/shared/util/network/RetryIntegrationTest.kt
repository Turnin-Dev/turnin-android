package com.peekr.data.shared.util.network

import java.net.SocketTimeoutException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

// 추가적인 통합 테스트 예제
class RetryIntegrationTest {
    @Test
    fun `실제 네트워크 호출 시뮬레이션`() = runTest {
        // Given
        var callCount = 0
        val networkCall: suspend () -> String = {
            callCount++
            when (callCount) {
                1 -> throw SocketTimeoutException("Network timeout")
                2 -> throw SocketTimeoutException("Connection refused")
                else -> "Network response"
            }
        }

        // When
        val result = retry(
            attempt = 3,
            initialDelayMillis = 10,
            maxDelayMillis = 100,
            factor = 2.0,
            block = networkCall,
        )

        // Then
        assertEquals("Network response", result)
        assertEquals(3, callCount)
    }

    @Test
    fun `실제 비즈니스 로직 재시도 시뮬레이션`() = runTest {
        // Given
        var attemptCount = 0
        val businessLogic: suspend () -> Int = {
            attemptCount++
            if (attemptCount <= 2) {
                throw SocketTimeoutException("Service temporarily unavailable")
            }
            42 // 성공 결과
        }

        // When
        val result = retry(
            attempt = 5,
            initialDelayMillis = 50,
            maxDelayMillis = 500,
            factor = 1.5,
            block = businessLogic,
        )

        // Then
        assertEquals(42, result)
        assertEquals(3, attemptCount)
    }
}
