package com.turnin.core.domain.common.error

import com.turnin.core.domain.common.BaseError
import com.turnin.core.domain.common.Result
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ResultErrorCatcherTest {
    @Test
    fun `예외 처리 - 예외를 지정한 에러로 변환해 방출한다`() = runTest {
        // given
        val exception = IllegalStateException("error!")
        val upstream = flow<Result<Unit, TestError>> {
            emit(Result.Loading)
            throw exception
        }

        // when
        val results = upstream.catchUnexpected(TestError::Unexpected).toList()

        // then
        assertEquals(
            listOf(Result.Loading, Result.Error(TestError.Unexpected(exception), message = "error!")),
            results,
        )
    }

    @Test(expected = CancellationException::class)
    fun `예외 처리 - 코루틴 취소 예외는 변환하지 않고 그대로 전파한다`() = runTest {
        // given
        val upstream = flow<Result<Unit, TestError>> { throw CancellationException("cancelled") }

        // when
        upstream.catchUnexpected(TestError::Unexpected).toList()
    }

    private sealed interface TestError : BaseError {
        data class Unexpected(val cause: Throwable) : TestError
    }
}
