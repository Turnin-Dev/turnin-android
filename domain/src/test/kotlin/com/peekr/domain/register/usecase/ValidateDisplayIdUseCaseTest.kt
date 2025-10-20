package com.peekr.domain.register.usecase

import com.peekr.core.domain.validation.CommonValidationError
import com.peekr.core.domain.validation.ValidationResult
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidateDisplayIdUseCaseTest {
    private val usecase = ValidateDisplayIdUseCase()

    @Test
    fun `사용자 표시 ID 유효성 검사 성공 테스트`() = runTest {
        val result = usecase(VALID_DISPLAY_ID).last()
        assertTrue(result is ValidationResult.Valid)
        assertEquals(VALID_DISPLAY_ID, (result as ValidationResult.Valid).value.value)
    }

    @Test
    fun `사용자 표시 ID 유효성 검사 실패 테스트 - 잘못된 형식`() = runTest {
        val result = usecase(INVALID_FORMAT_DISPLAY_ID).last()
        assertTrue(result is ValidationResult.Invalid)
        assertTrue((result as ValidationResult.Invalid).error is CommonValidationError.InvalidFormat)
    }

    @Test
    fun `사용자 표시 ID 유효성 검사 실패 테스트 - 길이 제약 위반`() = runTest {
        val result = usecase(TooLongDisplayId).last()
        assertTrue(result is ValidationResult.Invalid)
        assertTrue((result as ValidationResult.Invalid).error is CommonValidationError.TooShortOrLong)
    }

    @Test
    fun `사용자 표시 ID 유효성 검사 실패 테스트 - 빈 문자열`() = runTest {
        val result = usecase(EMPTY_DISPLAY_ID).last()
        assertTrue(result is ValidationResult.Invalid)
        assertTrue((result as ValidationResult.Invalid).error is CommonValidationError.Empty)
    }

    companion object {
        private const val VALID_DISPLAY_ID = "my_id"
        private const val INVALID_FORMAT_DISPLAY_ID = "my-id!!"
        private val TooLongDisplayId = "hello".repeat(100)
        private const val EMPTY_DISPLAY_ID = ""
    }
}
