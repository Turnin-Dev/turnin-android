package com.peekr.domain.account.usecase.register

import com.peekr.core.domain.validation.ValidationError
import com.peekr.core.domain.validation.ValidationResult
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidateNameUseCaseTest {
    private val usecase = ValidateNameUseCase()

    @Test
    fun `이름 유효성 검사 성공 테스트`() = runTest {
        val result = usecase(VALID_NAME).last()
        assertTrue(result is ValidationResult.Valid)
        assertEquals(VALID_NAME, (result as ValidationResult.Valid).value.value)
    }

    @Test
    fun `이름 유효성 검사 실패 테스트 - 잘못된 형식`() = runTest {
        val result = usecase(INVALID_FORMAT_NAME).last()
        assertTrue(result is ValidationResult.Invalid)
        assertTrue((result as ValidationResult.Invalid).error is ValidationError.Name.InvalidFormat)
    }

    @Test
    fun `이름 유효성 검사 실패 테스트 - 길이 제약 위반`() = runTest {
        val result = usecase(TooLongName).last()
        assertTrue(result is ValidationResult.Invalid)
        assertTrue((result as ValidationResult.Invalid).error is ValidationError.Name.TooShortOrLong)
    }

    @Test
    fun `이름 유효성 검사 실패 테스트 - 빈 문자열`() = runTest {
        val result = usecase(EMPTY_NAME).last()
        assertTrue(result is ValidationResult.Invalid)
        assertTrue((result as ValidationResult.Invalid).error is ValidationError.Name.Empty)
    }

    companion object {
        private const val VALID_NAME = "hong길동"
        private const val INVALID_FORMAT_NAME = "my-name!!"
        private val TooLongName = "hello".repeat(100)
        private const val EMPTY_NAME = ""
    }
}
