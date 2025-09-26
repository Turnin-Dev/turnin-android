package com.peekr.domain.keyword.usecase

import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.util.Result
import com.peekr.domain.keyword.model.Keyword
import com.peekr.domain.keyword.repository.KeywordRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetKeywordUseCaseTest {
    private val repository: KeywordRepository = mockk()
    private val usecase: GetKeywordUseCase = GetKeywordUseCase(repository)

    @Test
    fun `키워드 조회 - 성공 테스트`() = runTest {
        // given
        coEvery {
            repository.getKeyword(TestKeywordId)
        } returns flowOf(Result.Success(TestKeyword))

        // when
        val result = usecase(TestKeywordId).last()

        // then
        assertTrue(result is Result.Success)
        assertEquals((result as Result.Success).data, TestKeyword)
    }

    companion object {
        private val TestKeywordId = KeywordId(1L)
        private const val TEST_KEYWORD = "sample"
        private val TestKeyword = Keyword(
            id = TestKeywordId,
            keyword = TEST_KEYWORD,
            createdBy = 1L,
            createdAt = 1000,
            updatedAt = 1000,
        )
    }
}
