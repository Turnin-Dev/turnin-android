package com.peekr.domain.profile.usecase.my

import com.peekr.core.domain.common.validation.ValidationErrorType
import com.peekr.core.domain.common.validation.ValidationResult
import com.peekr.core.domain.model.KeywordValue
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidateKeywordUseCaseTest {
    private val usecase = ValidateKeywordUseCase()

    @Test
    fun `키워드 유효성 검사 성공`() = runTest {
        // when
        val result = usecase(VALID_KEYWORD)

        // then
        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun `키워드가 비어있을 때 유효성 검사 실패`() = runTest {
        // when
        val result = usecase(EMPTY_KEYWORD)

        // then
        val valid = result as ValidationResult.Invalid
        assertTrue(valid.error is ValidationErrorType.Common.Empty)
    }

    @Test
    fun `키워드가 길이를 초과했을 때 유효성 검사 실패`() = runTest {
        // when
        val result = usecase(TOO_LONG_KEYWORD)

        // then
        val valid = result as ValidationResult.Invalid
        assertTrue(valid.error is ValidationErrorType.Common.TooShortOrLong)
    }

    companion object {
        private const val VALID_KEYWORD = "sample"
        private const val EMPTY_KEYWORD = ""
        private val TOO_LONG_KEYWORD = "a".repeat(KeywordValue.MAX_LENGTH + 1)
    }
}
