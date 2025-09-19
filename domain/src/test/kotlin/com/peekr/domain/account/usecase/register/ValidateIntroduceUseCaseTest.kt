package com.peekr.domain.account.usecase.register

import com.peekr.domain.common.util.ValidationError
import com.peekr.domain.common.util.ValidationResult
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ValidateIntroduceUseCaseTest {
    private val usecase = ValidateIntroduceUseCase()

    @Test
    fun `소개 글 유효성 검사 성공 테스트`() = runTest {
        val result = usecase(VALID_INTRODUCE).last()
        assert(result is ValidationResult.Valid)
        assertEquals(VALID_INTRODUCE, (result as ValidationResult.Valid).value.value)
    }

    @Test
    fun `소개 글 유효성 검사 실패 테스트 - 길이 제약 위반`() = runTest {
        val result = usecase(TooLongIntroduce).last()
        assert(result is ValidationResult.Invalid)
        assert((result as ValidationResult.Invalid).error is ValidationError.Introduce.TooLong)
    }

    companion object {
        private const val VALID_INTRODUCE = "hello world!"
        private val TooLongIntroduce = "hello!".repeat(201)
    }
}
