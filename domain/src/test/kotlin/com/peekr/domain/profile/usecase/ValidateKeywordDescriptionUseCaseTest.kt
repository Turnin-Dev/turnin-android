package com.peekr.domain.profile.usecase

import com.peekr.core.domain.validation.ValidationResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

// 아직 키워드 설명(KeywordDescription)에 대한 규칙이 없기 때문에 유효성 검사는 무조건 성공한다.
// 추후 키워드 설명에 대한 비즈니스 규칙이 생긴다면 추후에 테스트 추가
class ValidateKeywordDescriptionUseCaseTest {
    private val usecase = ValidateKeywordDescriptionUseCase()

    @Test
    fun `키워드 설명 유효성 검사 성공`() = runTest {
        // when
        val result = usecase(VALID_DESC)

        // then
        assertTrue(result is ValidationResult.Valid)
    }

    companion object {
        private const val VALID_DESC = "hello!"
    }
}
