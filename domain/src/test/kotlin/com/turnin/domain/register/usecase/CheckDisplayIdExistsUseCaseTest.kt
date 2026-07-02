package com.turnin.domain.register.usecase

import com.turnin.core.domain.auth.repository.AuthRepository
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.model.DisplayId
import com.turnin.domain.register.error.RegisterErrorType
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CheckDisplayIdExistsUseCaseTest {
    private val repository: AuthRepository = mockk()
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
        assertTrue(result is Result.Success)
    }

    @Test
    fun `사용자 표시 ID 중복 검사 시 에러가 발생하면 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = CommonErrorType.Unexpected(null)
        every {
            repository.existsDisplayId(TestDisplayId)
        } returns flowOf(Result.Error(expectedError))

        // when
        val result = usecase(TestDisplayId.value).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(
            RegisterErrorType.CommonError(expectedError),
            (result as Result.Error).error,
        )
    }

    companion object {
        private val TestDisplayId = DisplayId("sample")
    }
}
