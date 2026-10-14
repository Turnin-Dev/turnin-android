package com.turnin.domain.profile.usecase.user

import com.turnin.core.domain.common.Result
import com.turnin.core.domain.friend.model.FriendStatus
import com.turnin.core.domain.model.DisplayId
import com.turnin.core.domain.model.Introduce
import com.turnin.core.domain.model.Name
import com.turnin.core.domain.model.UserId
import com.turnin.core.domain.user.model.CoreUserProfile
import com.turnin.core.domain.user.repository.UserRepository
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest

class GetUserProfileUseCaseTest {
    private val userRepository: UserRepository = mock()
    private val usecase = GetUserProfileUseCase(userRepository)

    @BeforeTest
    fun setUp() {
        every {
            userRepository.getUserProfile(TestUserId)
        } returns flowOf(Result.Success(TestCoreUserProfile))
    }

    @Test
    fun `사용자 프로필 조회 성공 테스트`() = runTest {
        // when
        val result = usecase(TestUserId.value).last()

        // then
        val success = result as Result.Success
        assertEquals(TestCoreUserProfile.friendStatus, success.data.friendStatus)
    }

    companion object {
        private val TestUserId = UserId(1L)
        private val TestDisplayId = DisplayId("did")
        private val TestCoreUserProfile = CoreUserProfile(
            userId = TestUserId,
            displayId = TestDisplayId,
            name = Name("name"),
            profileImageUrl = null,
            introduce = Introduce("hello"),
            lastLoginAt = 1000L,
            friendsCount = 50L,
            active = true,
            friendStatus = FriendStatus.NOTHING,
            isBlocked = false,
        )
    }
}
