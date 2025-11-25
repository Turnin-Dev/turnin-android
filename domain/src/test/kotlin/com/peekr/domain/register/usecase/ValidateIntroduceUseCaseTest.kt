package com.peekr.domain.register.usecase

import com.peekr.core.domain.common.validation.ValidationErrorType
import com.peekr.core.domain.common.validation.ValidationResult
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidateIntroduceUseCaseTest {
    private val usecase = ValidateIntroduceUseCase()

    @Test
    fun `소개 글 유효성 검사 성공 테스트`() = runTest {
        val result = usecase(VALID_INTRODUCE).last()
        assertTrue(result is ValidationResult.Valid)
        assertEquals(VALID_INTRODUCE, (result as ValidationResult.Valid).value.value)
    }

    @Test
    fun `소개 글 유효성 검사 실패 테스트 - 길이 제약 위반`() = runTest {
        val result = usecase(TooLongIntroduce).last()
        assertTrue(result is ValidationResult.Invalid)
        assertTrue((result as ValidationResult.Invalid).error is ValidationErrorType.Common.TooShortOrLong)
    }

    companion object {
        private const val VALID_INTRODUCE = "hello world!"
        private val TooLongIntroduce = "hello!".repeat(201)
    }
}
