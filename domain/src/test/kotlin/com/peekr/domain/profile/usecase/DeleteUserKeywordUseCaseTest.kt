package com.peekr.domain.profile.usecase

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class DeleteUserKeywordUseCaseTest {
    private val userKeywordRepository: UserKeywordRepository = mockk()
    private val usecase = DeleteUserKeywordUseCase(userKeywordRepository)

    @Test
    fun `사용자 키워드 삭제 성공 테스트`() = runTest {
        // given
        every {
            userKeywordRepository.deleteUserKeyword(TestUserKeywordId)
        } returns flowOf(Result.Success(Unit))

        // when
        val result = usecase(TestUserKeywordId).last()

        // then
        assertTrue(result is Result.Success)
    }

    companion object {
        private val TestUserKeywordId = UserKeywordId(1L)
    }
}
