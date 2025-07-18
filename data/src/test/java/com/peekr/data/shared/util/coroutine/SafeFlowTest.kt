package com.peekr.data.shared.util.coroutine

import com.peekr.domain.shared.util.ErrorType
import com.peekr.domain.shared.util.Result.Error
import com.peekr.domain.shared.util.Result.Success
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import timber.log.Timber

@OptIn(ExperimentalCoroutinesApi::class)
class SafeFlowTest {
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        // Timber 모킹
        val mockTree = mockk<Timber.Tree>(relaxed = true)
        Timber.plant(mockTree)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `safeResultFlow - 정상 동작 시 성공적으로 값을 emit한다`() = testScope.runTest {
        // Given
        val expectedValue = "test data"

        // When
        val flow = safeResultFlow<String>(testDispatcher) {
            emit(Success(expectedValue))
        }

        // Then
        val result = flow.first()
        assertTrue(result is Success)
        assertEquals(expectedValue, (result as Success).data)
    }

    @Test
    fun `safeResultFlow - 예외 발생 시 Error 결과를 emit한다`() = testScope.runTest {
        // Given
        val exception = RuntimeException("Test exception")
        val expectedErrorType = ErrorType.Exception.Unexpected

        // When
        val flow = safeResultFlow<String>(
            dispatcher = testDispatcher,
            errorMapper = { ErrorType.Exception.Unexpected },
        ) {
            throw exception
        }

        // Then
        val result = flow.first()
        assertTrue(result is Error)
        val errorResult = result as Error
        assertEquals(expectedErrorType, errorResult.error)
        assertEquals(exception.message, errorResult.message)
    }

    @Test
    fun `safeResultFlow - 로깅 활성화 시 예외를 로그에 기록한다`() = testScope.runTest {
        // Given
        val exception = RuntimeException("Test exception")

        // When
        val flow = safeResultFlow<String>(
            dispatcher = testDispatcher,
            enableLogging = true,
        ) {
            throw exception
        }

        // Then
        flow.first()
        verify { Timber.e(exception, "Exception in flow: ${exception.message}") }
    }

    @Test
    fun `safeResultFlow - 로깅 비활성화 시 예외를 로그에 기록하지 않는다`() = testScope.runTest {
        // Given
        val exception = RuntimeException("Test exception")

        // When
        val flow = safeResultFlow<String>(
            dispatcher = testDispatcher,
            enableLogging = false,
        ) {
            throw exception
        }

        // Then
        flow.first()
        verify(exactly = 0) { Timber.e(any<Throwable>(), any<String>()) }
    }

    @Test
    fun `safeResultFlow - 커스텀 errorMapper를 사용한다`() = testScope.runTest {
        // Given
        val exception = IllegalArgumentException("Invalid argument")
        val customErrorType = ErrorType.Exception.IO

        // When
        val flow = safeResultFlow<String>(
            dispatcher = testDispatcher,
            errorMapper = { throwable ->
                when (throwable) {
                    is IllegalArgumentException -> ErrorType.Exception.IO
                    else -> ErrorType.Exception.Unexpected
                }
            },
        ) {
            throw exception
        }

        // Then
        val result = flow.first()
        assertTrue(result is Error)
        assertEquals(customErrorType, (result as Error).error)
    }

    @Test
    fun `safeResultFlow - 여러 값을 emit할 수 있다`() = testScope.runTest {
        // Given
        val values = listOf("value1", "value2", "value3")

        // When
        val flow = safeResultFlow<String>(testDispatcher) {
            values.forEach { value ->
                emit(Success(value))
            }
        }

        // Then
        val results = flow.toList()
        assertEquals(3, results.size)
        values.forEachIndexed { index, expectedValue ->
            val result = results[index]
            assertTrue(result is Success)
            assertEquals(expectedValue, (result as Success).data)
        }
    }

    @Test
    fun `safeFlow - 정상 동작 시 성공적으로 값을 emit한다`() = testScope.runTest {
        // Given
        val expectedValue = "test data"

        // When
        val flow = safeFlow<String>(testDispatcher) {
            emit(expectedValue)
        }

        // Then
        val result = flow.first()
        assertEquals(expectedValue, result)
    }

    @Test
    fun `safeFlow - 예외 발생 시 onError 콜백을 호출한다`() = testScope.runTest {
        // Given
        val exception = RuntimeException("Test exception")
        var capturedError: Throwable? = null

        // When
        val flow = safeFlow<String>(
            dispatcher = testDispatcher,
            onError = { error ->
                capturedError = error
            },
        ) {
            throw exception
        }

        // Then
        flow.catch { }.toList() // catch로 예외를 잡아서 플로우가 완료되도록 함
        assertEquals(exception, capturedError)
    }

    @Test
    fun `safeFlow - 로깅 활성화 시 예외를 로그에 기록한다`() = testScope.runTest {
        // Given
        val exception = RuntimeException("Test exception")

        // When
        val flow = safeFlow<String>(
            dispatcher = testDispatcher,
            enableLogging = true,
        ) {
            throw exception
        }

        // Then
        flow.catch { }.toList()
        verify { Timber.e(exception, "Exception in flow: ${exception.message}") }
    }

    @Test
    fun `safeFlow - 로깅 비활성화 시 예외를 로그에 기록하지 않는다`() = testScope.runTest {
        // Given
        val exception = RuntimeException("Test exception")

        // When
        val flow = safeFlow<String>(
            dispatcher = testDispatcher,
            enableLogging = false,
        ) {
            throw exception
        }

        // Then
        flow.catch { }.toList()
        verify(exactly = 0) { Timber.e(any<Throwable>(), any<String>()) }
    }

    @Test
    fun `safeFlow - 커스텀 onError 콜백을 사용한다`() = testScope.runTest {
        // Given
        val exception = RuntimeException("Test exception")
        var onErrorCalled = false
        var capturedError: Throwable? = null

        // When
        val flow = safeFlow<String>(
            dispatcher = testDispatcher,
            onError = { error ->
                onErrorCalled = true
                capturedError = error
                emit("fallback value")
            },
        ) {
            throw exception
        }

        // Then
        val result = flow.first()
        assertTrue(onErrorCalled)
        assertEquals(exception, capturedError)
        assertEquals("fallback value", result)
    }

    @Test
    fun `safeFlow - 여러 값을 emit할 수 있다`() = testScope.runTest {
        // Given
        val values = listOf("value1", "value2", "value3")

        // When
        val flow = safeFlow<String>(testDispatcher) {
            values.forEach { value ->
                emit(value)
            }
        }

        // Then
        val results = flow.toList()
        assertEquals(values, results)
    }

    @Test
    fun `safeFlow - 기본 onError는 printStackTrace를 호출한다`() = testScope.runTest {
        // Given
        val exception = RuntimeException("Test exception")

        // When
        val flow = safeFlow<String>(testDispatcher) {
            throw exception
        }

        // Then
        // 기본 onError는 printStackTrace()를 호출하므로 예외가 발생해도 플로우가 완료됨
        val results = flow.catch { }.toList()
        assertTrue(results.isEmpty())
    }
}
