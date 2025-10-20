package com.peekr.domain.profile.usecase

import com.peekr.core.domain.user.model.DisplayId
import com.peekr.core.domain.user.model.Introduce
import com.peekr.core.domain.user.model.Name
import com.peekr.core.domain.util.Result
import com.peekr.domain.profile.model.Profile
import com.peekr.domain.profile.repository.ProfileRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class GetProfileUseCaseTest {
    private val profileRepository: ProfileRepository = mockk()
    private val usecase = GetProfileUseCase(profileRepository)

    @Test
    fun `사용자 프로필 조회 - 성공 테스트`() = runTest {
        // given
        every {
            profileRepository.getProfile()
        } returns flowOf(Result.Success(TestProfile))

        // when
        val result = usecase().last()

        // then
        assertTrue(result is Result.Success)
    }

    companion object {
        private val TestProfile = Profile(
            displayId = DisplayId("id"),
            name = Name("name"),
            friendsTotal = 10,
            profileImageUrl = null,
            introduce = Introduce("hello"),
            keywords = emptyList(),
        )
    }
}
