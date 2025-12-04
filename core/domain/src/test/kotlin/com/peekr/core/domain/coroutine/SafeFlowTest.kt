package com.peekr.core.domain.coroutine

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.coroutine.safeFlow
import com.peekr.core.domain.common.coroutine.safeResultFlow
import com.peekr.core.domain.common.error.CommonErrorType
import io.mockk.clearAllMocks
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
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SafeFlowTest {
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
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
        val flow = safeResultFlow<String, CommonErrorType>(testDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Success(expectedValue))
        }

        // Then
        val result = flow.first()
        Assert.assertTrue(result is Result.Success)
        Assert.assertEquals(expectedValue, (result as Result.Success).data)
    }

    @Test
    fun `safeResultFlow - 예외 발생 시 Error 결과를 emit한다`() = testScope.runTest {
        // Given
        val exception = RuntimeException("Test exception")
        val expectedErrorType = CommonErrorType.Unexpected(exception)

        // When
        val flow = safeResultFlow<String, CommonErrorType>(
            dispatcher = testDispatcher,
            unexpectedErrorMapper = { CommonErrorType.Unexpected(exception) },
        ) {
            throw exception
        }

        // Then
        val result = flow.first()
        Assert.assertTrue(result is Result.Error)
        val errorResult = result as Result.Error
        Assert.assertEquals(expectedErrorType, errorResult.error)
        Assert.assertEquals(exception.message, errorResult.message)
    }

    @Test
    fun `safeResultFlow - 커스텀 errorMapper를 사용한다`() = testScope.runTest {
        // Given
        val exception = IllegalArgumentException("Invalid argument")
        val customErrorType = CommonErrorType.Exception.IO

        // When
        val flow = safeResultFlow<String, CommonErrorType>(
            dispatcher = testDispatcher,
            unexpectedErrorMapper = { throwable ->
                when (throwable) {
                    is IllegalArgumentException -> CommonErrorType.Exception.IO
                    else -> CommonErrorType.Unexpected(throwable)
                }
            },
        ) {
            throw exception
        }

        // Then
        val result = flow.first()
        Assert.assertTrue(result is Result.Error)
        Assert.assertEquals(customErrorType, (result as Result.Error).error)
    }

    @Test
    fun `safeResultFlow - 여러 값을 emit할 수 있다`() = testScope.runTest {
        // Given
        val values = listOf("value1", "value2", "value3")

        // When
        val flow = safeResultFlow<String, CommonErrorType>(testDispatcher, { CommonErrorType.Unexpected(it) }) {
            values.forEach { value ->
                emit(Result.Success(value))
            }
        }

        // Then
        val results = flow.toList()
        Assert.assertEquals(3, results.size)
        values.forEachIndexed { index, expectedValue ->
            val result = results[index]
            Assert.assertTrue(result is Result.Success)
            Assert.assertEquals(expectedValue, (result as Result.Success).data)
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
        Assert.assertEquals(expectedValue, result)
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
        Assert.assertEquals(exception, capturedError)
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
        Assert.assertTrue(onErrorCalled)
        Assert.assertEquals(exception, capturedError)
        Assert.assertEquals("fallback value", result)
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
        Assert.assertEquals(values, results)
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
        Assert.assertTrue(results.isEmpty())
    }
}
