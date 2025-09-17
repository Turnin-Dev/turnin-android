package com.peekr.domain.keyword.usecase

import com.peekr.domain.common.model.KeywordId
import com.peekr.domain.common.util.Result
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

class CreateKeywordUseCaseTest {
    private val repository: KeywordRepository = mockk()
    private val usecase: CreateKeywordUseCase = CreateKeywordUseCase(repository)

    @Test
    fun `키워드 생성 - 성공 테스트`() = runTest {
        // given
        coEvery {
            repository.createKeyword(TEST_KEYWORD)
        } returns flowOf(Result.Success(TestKeyword))

        // when
        val result = usecase(TEST_KEYWORD).last()

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
