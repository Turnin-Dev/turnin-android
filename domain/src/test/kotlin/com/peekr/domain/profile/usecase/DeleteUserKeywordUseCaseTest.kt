package com.peekr.domain.profile.usecase

import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.util.Result
import com.peekr.domain.profile.repository.ProfileRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class DeleteUserKeywordUseCaseTest {
    private val profileRepository: ProfileRepository = mockk()
    private val usecase = DeleteUserKeywordUseCase(profileRepository)

    @Test
    fun `사용자 키워드 삭제 성공 테스트`() = runTest {
        // given
        every {
            profileRepository.deleteKeyword(TestUserKeywordId)
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
