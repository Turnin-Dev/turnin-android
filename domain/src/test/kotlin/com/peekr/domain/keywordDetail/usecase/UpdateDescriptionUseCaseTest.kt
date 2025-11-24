package com.peekr.domain.keywordDetail.usecase

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.userKeyword.model.PatchDescription
import com.peekr.domain.keywordDetail.repository.KeywordDetailRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class UpdateDescriptionUseCaseTest {
    private val keywordDetailRepository: KeywordDetailRepository = mockk()
    private val usecase = UpdateDescriptionUseCase(keywordDetailRepository)

    @Test
    fun `키워드 설명 수정 성공 테스트`() = runTest {
        // given
        every {
            keywordDetailRepository.updateDescription(
                userKeywordId = TEST_USER_KEYWORD_ID,
                description = TestDescription.value,
            )
        } returns flowOf(Result.Success(TestPatchDescription))

        // when
        val result = usecase(TEST_USER_KEYWORD_ID, TestDescription.value).last()

        // then
        val success = result as Result.Success
        assertEquals(TestPatchDescription, success.data)
    }

    companion object {
        private const val TEST_USER_KEYWORD_ID = 1L
        private val TestDescription = KeywordDescription("sample")
        private val TestPatchDescription = PatchDescription(TestDescription)
    }
}
