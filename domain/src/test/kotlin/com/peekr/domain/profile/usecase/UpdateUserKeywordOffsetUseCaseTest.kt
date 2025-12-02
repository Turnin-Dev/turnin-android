package com.peekr.domain.profile.usecase

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.userKeyword.model.PatchOffset
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import com.peekr.domain.profile.usecase.my.UpdateUserKeywordOffsetUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class UpdateUserKeywordOffsetUseCaseTest {
    private val userKeywordRepository: UserKeywordRepository = mockk()
    private val usecase = UpdateUserKeywordOffsetUseCase(userKeywordRepository)

    @Test
    fun `사용자 키워드 오프셋 수정 성공 테스트`() = runTest {
        // given
        every {
            userKeywordRepository.patchOffset(TestUserKeywordId, TestPatchOffset)
        } returns flowOf(Result.Success(TestPatchOffset))

        // when
        val expectedOffsetX = TestPatchOffset.offsetX.toFloat()
        val expectedOffsetY = TestPatchOffset.offsetY.toFloat()
        val result = usecase(
            userKeywordId = TestUserKeywordId,
            offsetX = expectedOffsetX,
            offsetY = expectedOffsetY,
        ).last()

        // then
        val success = result as Result.Success
        assertEquals(expectedOffsetX, success.data.offsetX.toFloat())
        assertEquals(expectedOffsetY, success.data.offsetY.toFloat())
    }

    companion object {
        private val TestUserKeywordId = UserKeywordId(1L)
        private val TestPatchOffset = PatchOffset(1.0, 2.0)
    }
}
