package com.peekr.domain.profile.usecase.my

import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.Introduce
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.user.model.CoreMyProfile
import com.peekr.core.domain.user.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class GetMyProfileUseCaseTest {
    private val userRepository: UserRepository = mockk()
    private val usecase = GetMyProfileUseCase(userRepository)

    @Test
    fun `나의 프로필 조회 성공 테스트`() = runTest {
        // given
        every { userRepository.myProfile } returns MutableStateFlow(TestCoreMyProfile)

        // when
        val result = usecase().first { it != null }
        assertNotNull(result)
        val actual = CoreMyProfile(
            userId = result!!.userId,
            displayId = result.displayId,
            name = result.name,
            profileImageUrl = result.profileImageUrl,
            introduce = result.introduce,
            lastLoginAt = result.lastLoginAt,
            friendsCount = result.friendsCount,
            active = result.active,
        )

        // then
        assertEquals(TestMyUserId, actual.userId)
        assertEquals(TestCoreMyProfile.displayId, actual.displayId)
        assertEquals(TestCoreMyProfile.name, actual.name)
    }

    companion object {
        private val TestMyUserId = UserId(1L)
        private val TestCoreMyProfile = CoreMyProfile(
            userId = TestMyUserId,
            displayId = DisplayId("did"),
            name = Name("name"),
            profileImageUrl = null,
            introduce = Introduce("hello"),
            lastLoginAt = 1000L,
            friendsCount = 50L,
            active = true,
        )
    }
}
