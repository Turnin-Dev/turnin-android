package com.peekr.domain.profile.usecase.my

import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.model.KeywordName
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordDetail
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.user.repository.UserRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetMyKeywordsUseCaseTest {
    private val userRepository: UserRepository = mockk()
    private val usecase = GetMyKeywordsUseCase(userRepository)

    @Test
    fun `나의 키워드 리스트 조회 성공 테스트`() = runTest {
        // given
        val expectedCount = 2
        val expectedList = List(expectedCount) { TestUserKeywordDetail }
        coEvery {
            userRepository.getMyKeywords()
        } returns flowOf(expectedList)

        // when
        val result = usecase().last()

        // then
        assertEquals(expectedCount, result.size)
        assertEquals(expectedList, result)
    }

    companion object {
        private val TestMyUserId = UserId(1L)
        private val TestUserKeywordDetail = UserKeywordDetail(
            userKeywordId = UserKeywordId(1L),
            keywordId = KeywordId(1L),
            keywordName = KeywordName("key"),
            description = KeywordDescription("hello"),
            userInfo = null,
            createdAt = 1000,
            updatedAt = 1000,
        )
    }
}
