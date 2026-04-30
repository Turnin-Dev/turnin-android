package com.turnin.domain.profile.usecase.user

import com.turnin.core.domain.common.Result
import com.turnin.core.domain.model.KeywordDescription
import com.turnin.core.domain.model.KeywordId
import com.turnin.core.domain.model.KeywordName
import com.turnin.core.domain.model.Name
import com.turnin.core.domain.model.UserId
import com.turnin.core.domain.model.UserKeywordId
import com.turnin.core.domain.userKeyword.model.UserInfo
import com.turnin.core.domain.userKeyword.model.UserKeywordDetail
import com.turnin.core.domain.userKeyword.model.toNonDetail
import com.turnin.core.domain.userKeyword.repository.UserKeywordRepository
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetUserKeywordsUseCaseTest {
    private val userKeywordRepository: UserKeywordRepository = mockk()
    private val usecase = GetUserKeywordsUseCase(userKeywordRepository)

    @Test
    fun `사용자 키워드 리스트 조회 성공 테스트`() = runTest {
        // given
        val expectedCount = 2
        val expectedList = List(expectedCount) { TestUserKeywordDetail }
        every {
            userKeywordRepository.getUserKeywords(TestUserId)
        } returns flowOf(Result.Success(expectedList))

        // when
        val result = usecase(TestUserId.value).last()

        // then
        val success = result as Result.Success
        assertEquals(expectedCount, success.data.size)
        assertEquals(expectedList.map { it.toNonDetail() }, success.data)
    }

    companion object {
        private val TestUserId = UserId(1L)
        private val TestUserKeywordDetail = UserKeywordDetail(
            userKeywordId = UserKeywordId(1L),
            keywordId = KeywordId(1L),
            keywordName = KeywordName("name"),
            description = KeywordDescription("description"),
            userInfo = UserInfo(
                userId = TestUserId,
                userName = Name("name"),
                profileImageUrl = null,
            ),
            createdAt = 1000L,
            updatedAt = 1000L,
        )
    }
}
