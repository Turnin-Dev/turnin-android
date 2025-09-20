package com.peekr.domain.userKeyword.usecase

import com.peekr.domain.common.model.UserId
import com.peekr.domain.common.model.UserKeywordId
import com.peekr.domain.common.util.Result
import com.peekr.domain.userKeyword.model.PatchUserKeyword
import com.peekr.domain.userKeyword.repository.UserKeywordRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class PatchUserKeywordUseCaseTest {
    private val repository: UserKeywordRepository = mockk()
    private val usecase = PatchUserKeywordUseCase(repository)

    @Test
    fun `사용자 키워드 수정 성공 테스트`() = runTest {
        // given
        every {
            repository.patchUserKeyword(TestUserId, TestUserKeywordId, TestPatchUserKeyword)
        } returns flowOf(Result.Success(Unit))

        // when
        val result = usecase(TestUserId, TestUserKeywordId, TestPatchUserKeyword).last()

        // then
        assertTrue(result is Result.Success)
    }

    companion object {
        private val TestUserId = UserId(1L)
        private val TestUserKeywordId = UserKeywordId(1L)
        private val TestPatchUserKeyword = PatchUserKeyword(
            offsetX = 0.0,
            offsetY = 0.0,
            description = "sample",
        )
    }
}
