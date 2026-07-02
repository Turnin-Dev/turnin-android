package com.turnin.domain.keywordDetail.usecase

import com.turnin.core.domain.common.Result
import com.turnin.core.domain.model.KeywordDescription
import com.turnin.core.domain.model.KeywordId
import com.turnin.core.domain.model.KeywordName
import com.turnin.core.domain.model.Name
import com.turnin.core.domain.model.UserId
import com.turnin.core.domain.model.UserKeywordId
import com.turnin.core.domain.userKeyword.model.UserInfo
import com.turnin.core.domain.userKeyword.model.UserKeywordDetail
import com.turnin.core.domain.userKeyword.repository.UserKeywordRepository
import com.turnin.domain.keywordDetail.model.toKeywordDetail
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class RefreshKeywordDetailUseCaseTest {
    private val userKeywordRepository: UserKeywordRepository = mockk()
    private val usecase = RefreshKeywordDetailUseCase(userKeywordRepository)

    @Test
    fun `키워드 상세 정보 새로고침 성공 테스트`() = runTest {
        // given
        every {
            userKeywordRepository.getDetailRefresh(TestUserId, TestUserKeywordId)
        } returns flowOf(Result.Success(TestUserKeywordDetail))

        // when
        val result = usecase(TestUserId.value, TestUserKeywordId.value).last()

        // then
        val success = result as Result.Success
        assertEquals(TestUserKeywordDetail.toKeywordDetail(), success.data)
    }

    companion object {
        private val TestUserId = UserId(100L)
        private val TestUserKeywordId = UserKeywordId(100L)
        private val TestUserKeywordDetail = UserKeywordDetail(
            userKeywordId = TestUserKeywordId,
            keywordId = KeywordId(1L),
            keywordName = KeywordName("k_name"),
            description = KeywordDescription("desc"),
            userInfo = UserInfo(
                userId = TestUserId,
                userName = Name("name"),
                profileImageUrl = null,
            ),
            createdAt = 0L,
            updatedAt = 0L,
        )
    }
}
