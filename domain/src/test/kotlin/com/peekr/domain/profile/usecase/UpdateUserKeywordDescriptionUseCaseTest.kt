package com.peekr.domain.profile.usecase

import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.userKeyword.model.PatchDescription
import com.peekr.core.domain.util.Result
import com.peekr.domain.profile.repository.ProfileRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class UpdateUserKeywordDescriptionUseCaseTest {
    private val profileRepository: ProfileRepository = mockk()
    private val usecase = UpdateUserKeywordDescriptionUseCase(profileRepository)

    @Test
    fun `사용자 키워드 설명 수정 성공 테스트`() = runTest {
        // given
        every {
            profileRepository.updateDescription(TestUserKeywordId, TestPatchDescription)
        } returns flowOf(Result.Success(TestPatchDescription))

        // when
        val result = usecase(TestUserKeywordId, TestPatchDescription.description.value).last()

        // then
        val success = result as Result.Success
        assertEquals(TestPatchDescription.description.value, success.data.description.value)
    }

    companion object {
        private val TestUserKeywordId = UserKeywordId(1L)
        private val TestPatchDescription = PatchDescription(KeywordDescription("hello"))
    }
}
