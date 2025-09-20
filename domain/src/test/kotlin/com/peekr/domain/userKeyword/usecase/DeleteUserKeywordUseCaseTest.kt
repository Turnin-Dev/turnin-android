package com.peekr.domain.userKeyword.usecase

import com.peekr.domain.common.model.UserId
import com.peekr.domain.common.model.UserKeywordId
import com.peekr.domain.common.util.Result
import com.peekr.domain.userKeyword.repository.UserKeywordRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class DeleteUserKeywordUseCaseTest {
    private val repository: UserKeywordRepository = mockk()
    private val usecase = DeleteUserKeywordUseCase(repository)

    @Test
    fun `사용자 키워드 삭제 성공 테스트`() = runTest {
        // given
        every {
            repository.deleteUserKeyword(TestUserId, TestUserKeywordId)
        } returns flowOf(Result.Success(Unit))

        // when
        val result = usecase(TestUserId, TestUserKeywordId).last()

        // then
        assertTrue(result is Result.Success)
    }

    companion object {
        private val TestUserId = UserId(1L)
        private val TestUserKeywordId = UserKeywordId(1L)
    }
}
