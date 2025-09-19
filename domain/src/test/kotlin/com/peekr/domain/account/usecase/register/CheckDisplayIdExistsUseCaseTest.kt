package com.peekr.domain.account.usecase.register

import com.peekr.domain.account.repository.AccountRepository
import com.peekr.domain.common.model.DisplayId
import com.peekr.domain.common.util.ErrorType
import com.peekr.domain.common.util.Result
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class CheckDisplayIdExistsUseCaseTest {
    private val repository: AccountRepository = mockk()
    private val usecase = CheckDisplayIdExistsUseCase(repository)

    @Test
    fun `사용자 표시 ID 중복 검사 성공 테스트`() = runTest {
        // given
        every {
            repository.existsDisplayId(TestDisplayId)
        } returns flowOf(Result.Success(true))

        // when
        val result = usecase(TestDisplayId.value).last()

        // then
        assert(result is Result.Success)
    }

    @Test
    fun `사용자 표시 ID 중복 검사 시 에러가 발생하면 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = ErrorType.Exception.IO
        every {
            repository.existsDisplayId(TestDisplayId)
        } returns flowOf(Result.Error(expectedError))

        // when
        val result = usecase(TestDisplayId.value).last()

        // then
        assert(result is Result.Error)
        assertEquals(expectedError, (result as Result.Error).error)
    }

    companion object {
        private val TestDisplayId = DisplayId("sample")
    }
}
