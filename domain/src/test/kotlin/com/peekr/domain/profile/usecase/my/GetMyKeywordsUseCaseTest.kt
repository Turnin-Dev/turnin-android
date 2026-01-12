package com.peekr.domain.profile.usecase.my

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.model.KeywordName
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.user.repository.UserRepository
import com.peekr.core.domain.userKeyword.model.UserKeyword
import com.peekr.core.domain.userKeyword.model.UserKeywords
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetMyKeywordsUseCaseTest {
    private val userRepository: UserRepository = mockk()
    private val userKeywordRepository: UserKeywordRepository = mockk()
    private val usecase = GetMyKeywordsUseCase(userRepository, userKeywordRepository)

    @Before
    fun setUp() {
        coEvery {
            userRepository.getUserId()
        } returns TestMyUserId
        coEvery {
            userKeywordRepository.getUserKeywords(TestMyUserId)
        } returns flowOf(Result.Success(TestUserKeywords))
    }

    @Test
    fun `나의 사용자 키워드 조회 성공 테스트`() = runTest {
        // when
        val result = usecase().last()

        // then
        val success = result as Result.Success
        assertEquals(TestUserKeywords, success.data)
    }

    companion object {
        private val TestMyUserId = UserId(1L)
        private val TestUserKeyword = UserKeyword(
            id = UserKeywordId(1L),
            keywordId = KeywordId(1L),
            keyword = KeywordName("key"),
            userId = UserId(1L),
            description = KeywordDescription("hello"),
            createdAt = 1000,
            updatedAt = 1000,
        )
        private val TestUserKeywords = UserKeywords(listOf(TestUserKeyword))
    }
}
