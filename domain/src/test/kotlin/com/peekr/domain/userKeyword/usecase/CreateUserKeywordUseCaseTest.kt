package com.peekr.domain.userKeyword.usecase

import com.peekr.domain.common.model.KeywordId
import com.peekr.domain.common.model.UserId
import com.peekr.domain.common.model.UserKeywordId
import com.peekr.domain.common.util.Result
import com.peekr.domain.userKeyword.model.CreateUserKeyword
import com.peekr.domain.userKeyword.model.UserKeyword
import com.peekr.domain.userKeyword.repository.UserKeywordRepository
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class CreateUserKeywordUseCaseTest {
    private val repository: UserKeywordRepository = mockk()
    private val usecase = CreateUserKeywordUseCase(repository)

    @Test
    fun `사용자 키워드 생성 성공 테스트`() = runTest {
        // given
        every {
            repository.createUserKeyword(TestCreateUserKeyword)
        } returns flowOf(Result.Success(TestUserKeyword))

        // when
        val result = usecase(TestCreateUserKeyword).last()

        // then
        assertTrue(result is Result.Success)
        assertEquals(TestUserKeyword, (result as Result.Success).data)
    }

    companion object {
        private val TestCreateUserKeyword = CreateUserKeyword(
            userId = UserId(1L),
            keywordId = KeywordId(1L),
            offsetX = 0.0,
            offsetY = 0.0,
            description = "sample",
        )
        private val TestUserKeyword = UserKeyword(
            id = UserKeywordId(1L),
            userId = UserId(1L),
            keywordId = KeywordId(1L),
            offsetX = 0.0,
            offsetY = 0.0,
            description = "",
            createdAt = 1000,
            updatedAt = 1000,
        )
    }
}
