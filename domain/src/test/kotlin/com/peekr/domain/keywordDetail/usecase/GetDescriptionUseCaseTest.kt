package com.peekr.domain.keywordDetail.usecase

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetDescriptionUseCaseTest {
    private val userKeywordRepository: UserKeywordRepository = mockk()
    private val usecase = GetDescriptionUseCase(userKeywordRepository)

    @Test
    fun `키워드 설명 조회 성공 테스트`() = runTest {
        // given
        every {
            userKeywordRepository.getDescription(TestUserKeywordId)
        } returns flowOf(Result.Success(TestKeywordDescription))

        // when
        val result = usecase(TestUserKeywordId.value).last()

        // then
        val success = result as Result.Success
        assertEquals(TestKeywordDescription, success.data)
    }

    companion object {
        private val TestUserKeywordId = UserKeywordId(1L)
        private val TestKeywordDescription = KeywordDescription("")
    }
}
