package com.peekr.domain.profile.usecase.userKeyword

import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.userKeyword.model.UserKeyword
import com.peekr.core.domain.userKeyword.model.UserKeywords
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import com.peekr.core.domain.util.Result
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetUserKeywordsUseCaseTest {
    private val repository: UserKeywordRepository = mockk()
    private val usecase = GetUserKeywordsUseCase(repository)

    @Test
    fun `사용자 키워드 리스트 조회 성공 테스트`() = runTest {
        // given
        every {
            repository.getUserKeywords(TestUserId)
        } returns flowOf(Result.Success(TestUserKeywords))

        // when
        val result = usecase(TestUserId).last()

        // then
        assertTrue(result is Result.Success)
        assertEquals(TestUserKeywords, (result as Result.Success).data)
    }

    companion object {
        private val TestUserId = UserId(1L)
        private val TestUserKeyword = UserKeyword(
            id = UserKeywordId(1L),
            userId = TestUserId,
            keywordId = KeywordId(1L),
            offsetX = 0.0,
            offsetY = 0.0,
            description = "sample",
            createdAt = 1000,
            updatedAt = 1000,
        )
        private val TestUserKeywords = UserKeywords(
            keywords = listOf(TestUserKeyword),
        )
    }
}
