package com.peekr.domain.profile.usecase

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.Introduce
import com.peekr.core.domain.model.Name
import com.peekr.domain.profile.model.ProfilePatch
import com.peekr.domain.profile.repository.ProfileRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateProfileUseCaseTest {
    private val profileRepository: ProfileRepository = mockk()
    private val usecase = UpdateProfileUseCase(profileRepository)

    @Test
    fun `사용자 프로필 수정 - 성공 테스트`() = runTest {
        // given
        every {
            profileRepository.updateProfile(any())
        } returns flowOf(Result.Success(Unit))

        // when
        val result = usecase(TestProfilePatch).last()

        // then
        assertTrue(result is Result.Success)
    }

    companion object {
        private val TestProfilePatch = ProfilePatch(
            displayId = DisplayId("id"),
            name = Name("name"),
            profileImageUrl = null,
            introduce = Introduce("hello"),
        )
    }
}
