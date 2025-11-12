package com.peekr.domain.keywordDetail.usecase

import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.util.Result
import com.peekr.domain.keywordDetail.repository.KeywordDetailRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetDescriptionUseCaseTest {
    private val keywordDetailRepository: KeywordDetailRepository = mockk()
    private val usecase = GetDescriptionUseCase(keywordDetailRepository)

    @Test
    fun `키워드 설명 조회 성공 테스트`() = runTest {
        // given
        every {
            keywordDetailRepository.getDescription(TEST_USER_KEYWORD_ID)
        } returns flowOf(Result.Success(TestKeywordDescription))

        // when
        val result = usecase(TEST_USER_KEYWORD_ID).last()

        // then
        val success = result as Result.Success
        assertEquals(TestKeywordDescription, success.data)
    }

    companion object {
        private const val TEST_USER_KEYWORD_ID = 1L
        private val TestKeywordDescription = KeywordDescription("")
    }
}
