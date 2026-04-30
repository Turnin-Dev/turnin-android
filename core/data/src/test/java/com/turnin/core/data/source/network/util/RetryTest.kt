package com.turnin.core.data.source.network.util

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RetryTest {
    @Test
    fun `성공적인 첫 번째 시도시 즉시 결과 반환`() = runTest {
        // Given
        val expectedResult = "success"
        val mockBlock = mockk<suspend () -> String>()
        coEvery { mockBlock() } returns expectedResult

        // When
        val result = retry(block = mockBlock)

        // Then
        assertEquals(expectedResult, result)
        coVerify(exactly = 1) { mockBlock() }
    }

    @Test
    fun `두 번째 시도에서 성공시 올바른 결과 반환`() = runTest {
        // Given
        val expectedResult = "success"
        val mockBlock = mockk<suspend () -> String>()
        coEvery { mockBlock() } throws
            SocketTimeoutException() andThen
            expectedResult

        // When
        val result = retry(attempt = 2, block = mockBlock)

        // Then
        assertEquals(expectedResult, result)
        coVerify(exactly = 2) { mockBlock() }
    }

    @Test
    fun `모든 재시도 실패시 마지막 예외 발생`() = runTest {
        // Given
        val expectedException = TimeoutException("Timeout")
        val mockBlock = mockk<suspend () -> String>()
        coEvery { mockBlock() } throws
            expectedException andThenThrows
            expectedException andThenThrows
            expectedException

        // When
        val actualException = runCatching {
            retry(attempt = 2, block = mockBlock)
        }.exceptionOrNull()

        // Then
        assertEquals(expectedException, actualException)
        coVerify(exactly = 3) { mockBlock() } // attempt(2) + 마지막 시도(1) = 3번
    }

    @Test
    fun `재시도 횟수가 0일 때 한 번만 실행`() = runTest {
        // Given
        val expectedResult = "success"
        val mockBlock = mockk<suspend () -> String>()
        coEvery { mockBlock() } returns expectedResult

        // When
        val result = retry(attempt = 0, block = mockBlock)

        // Then
        assertEquals(expectedResult, result)
        coVerify(exactly = 1) { mockBlock() }
    }

    @Test
    fun `지연 시간 계산 확인`() = runTest {
        // Given
        val expectedResult = "success"
        val testScheduler = testScheduler
        val mockBlock = mockk<suspend () -> String>()
        coEvery { mockBlock() } throws
            SocketTimeoutException() andThenThrows
            SocketTimeoutException() andThen
            expectedResult

        // When
        val startTime = testScheduler.currentTime
        val result = retry(
            attempt = 2,
            initialDelayMillis = 100,
            maxDelayMillis = 1000,
            factor = 2.0,
            block = mockBlock,
        )

        // Then
        assertEquals(expectedResult, result)
        coVerify(exactly = 3) { mockBlock() }
        // 지연이 발생했는지 확인
        assertTrue(testScheduler.currentTime > startTime)
    }

    @Test
    fun `최대 지연 시간 제한 확인`() = runTest {
        // Given
        val expectedResult = "success"
        val mockBlock = mockk<suspend () -> String>()
        coEvery { mockBlock() } throws
            SocketTimeoutException() andThen
            expectedResult

        // When
        val result = retry(
            attempt = 1,
            initialDelayMillis = 1000,
            maxDelayMillis = 500, // initialDelayMillis보다 작은 값
            factor = 2.0,
            block = mockBlock,
        )

        // Then
        assertEquals(expectedResult, result)
        coVerify(exactly = 2) { mockBlock() }
    }

    @Test
    fun `다양한 예외 타입에 대한 재시도`() = runTest {
        // Given
        val expectedResult = "success"
        val mockBlock = mockk<suspend () -> String>()
        coEvery { mockBlock() } throws
            SocketTimeoutException() andThenThrows
            UnknownHostException() andThen
            expectedResult

        // When
        val result = retry(attempt = 2, block = mockBlock)

        // Then
        assertEquals(expectedResult, result)
        coVerify(exactly = 3) { mockBlock() }
    }

    @Test
    fun `attempt가 1일 때 총 2번 시도`() = runTest {
        // Given
        val expectedResult = "success"
        val mockBlock = mockk<suspend () -> String>()
        coEvery { mockBlock() } throws
            SocketTimeoutException() andThen
            expectedResult

        // When
        val result = retry(attempt = 1, block = mockBlock)

        // Then
        assertEquals(expectedResult, result)
        coVerify(exactly = 2) { mockBlock() }
    }

    @Test
    fun `큰 attempt 값에 대한 테스트`() = runTest {
        // Given
        val expectedResult = "success"
        val mockBlock = mockk<suspend () -> String>()
        coEvery { mockBlock() } throws
            SocketTimeoutException("Error 1") andThenThrows
            SocketTimeoutException("Error 2") andThenThrows
            SocketTimeoutException("Error 3") andThenThrows
            SocketTimeoutException("Error 4") andThen
            expectedResult

        // When
        val result = retry(attempt = 4, block = mockBlock)

        // Then
        assertEquals(expectedResult, result)
        coVerify(exactly = 5) { mockBlock() } // attempt(4) + 마지막 시도(1) = 5번
    }

    @Test
    fun `factor 값에 따른 지연 시간 증가 확인`() = runTest {
        // Given
        val testScheduler = testScheduler
        val mockBlock = mockk<suspend () -> String>()
        coEvery { mockBlock() } throws
            SocketTimeoutException("Error 1") andThenThrows
            SocketTimeoutException("Error 2") andThen
            "success"

        // When
        val startTime = testScheduler.currentTime
        retry(
            attempt = 2,
            initialDelayMillis = 100,
            maxDelayMillis = 10000,
            factor = 3.0, // 더 큰 factor 값
            block = mockBlock,
        )

        // Then
        val totalTime = testScheduler.currentTime - startTime
        // factor가 클수록 지연 시간이 더 길어져야 함
        assertTrue(totalTime > 0)
    }

    @Test
    fun `null 반환값 처리`() = runTest {
        // Given
        val mockBlock = mockk<suspend () -> String?>()
        coEvery { mockBlock() } returns null

        // When
        val result = retry(block = mockBlock)

        // Then
        assertNull(result)
        coVerify(exactly = 1) { mockBlock() }
    }

    @Test
    fun `연속된 실패 후 성공 패턴 확인`() = runTest {
        // Given
        val mockBlock = mockk<suspend () -> String>()
        var callCount = 0
        coEvery { mockBlock() } answers {
            callCount++
            when (callCount) {
                1 -> throw SocketTimeoutException("First failure")
                2 -> throw SocketTimeoutException("Second failure")
                else -> "Final success"
            }
        }

        // When
        val result = retry(attempt = 3, block = mockBlock)

        // Then
        assertEquals("Final success", result)
        assertEquals(3, callCount)
        coVerify(exactly = 3) { mockBlock() }
    }

    @Test
    fun `음수 attempt 값 처리 테스트`() = runTest {
        // Given
        val mockBlock = mockk<suspend () -> String>()
        coEvery { mockBlock() } returns "success"

        // When
        val exception = runCatching {
            retry(attempt = -1, block = mockBlock)
        }.exceptionOrNull()

        // Then
        assertTrue(exception is IllegalArgumentException)
    }
}
