package com.turnin.domain.profile.usecase.user

import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.friend.model.Friend
import com.turnin.core.domain.friend.model.FriendId
import com.turnin.core.domain.friend.model.FriendRequestStatus
import com.turnin.core.domain.friend.model.FriendStatus
import com.turnin.core.domain.friend.repository.FriendRepository
import com.turnin.core.domain.model.UserId
import com.turnin.core.domain.user.repository.UserRepository
import com.turnin.domain.profile.error.ProfileErrorType
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UpdateFriendStateUseCaseTest {
    private val userRepository: UserRepository = mockk()
    private val friendRepository: FriendRepository = mockk()
    private val usecase = UpdateFriendStateUseCase(userRepository, friendRepository)

    @Before
    fun setUp() {
        coEvery { userRepository.getMyUserId() } returns TestRequesterId
        every {
            friendRepository.addFriend(any())
        } returns flowOf(Result.Success(TestFriend))
        every {
            friendRepository.deleteFriend(any())
        } returns flowOf(Result.Success(Unit))
        every {
            friendRepository.updateFriendStatus(any())
        } returns flowOf(Result.Success(Unit))
    }

    @Test
    fun `"NOTHING" 상태에서 친구 추가 요청 시 정상적으로 처리되고 "REQUESTED" 상태를 반환한다`() = runTest {
        // when
        val currentFriendStatus = FriendStatus.NOTHING
        val result = usecase(TestReceiverId.value, currentFriendStatus).last()

        // then
        val success = result as Result.Success
        assertEquals(FriendStatus.REQUESTED, currentFriendStatus.toggle())
        assertEquals(FriendStatus.REQUESTED, success.data)
    }

    @Test
    fun `"FRIENDS" 상태에서 친구 삭제 요청 시 정상적으로 처리되고 "NOTHING" 상태를 반환한다`() = runTest {
        // when
        val currentFriendStatus = FriendStatus.FRIENDS
        val result = usecase(TestReceiverId.value, currentFriendStatus).last()

        // then
        val success = result as Result.Success
        assertEquals(FriendStatus.NOTHING, currentFriendStatus.toggle())
        assertEquals(FriendStatus.NOTHING, success.data)
    }

    @Test
    fun `"REQUESTED" 상태에서 친구 삭제 요청 시 정상적으로 처리되고 "NOTHING" 상태를 반환한다`() = runTest {
        // when
        val currentFriendStatus = FriendStatus.REQUESTED
        val result = usecase(TestReceiverId.value, currentFriendStatus).last()

        // then
        val success = result as Result.Success
        assertEquals(FriendStatus.NOTHING, currentFriendStatus.toggle())
        assertEquals(FriendStatus.NOTHING, success.data)
    }

    @Test
    fun `"RECEIVED" 상태에서 친구 삭제 요청 시 정상적으로 처리되고 "FRIENDS" 상태를 반환한다`() = runTest {
        // when
        val currentFriendStatus = FriendStatus.RECEIVED
        val result = usecase(TestReceiverId.value, currentFriendStatus).last()

        // then
        val success = result as Result.Success
        assertEquals(FriendStatus.FRIENDS, currentFriendStatus.toggle())
        assertEquals(FriendStatus.FRIENDS, success.data)
    }

    @Test
    fun `사용자 ID를 찾지 못하는 경우 에러를 반환한다`() = runTest {
        // given
        coEvery { userRepository.getMyUserId() } returns null

        // when
        val currentFriendStatus = FriendStatus.NOTHING
        val result = usecase(TestReceiverId.value, currentFriendStatus).last()

        // then
        val error = result as Result.Error
        assertEquals(ProfileErrorType.MyUserIdNotFound, error.error)
    }

    @Test
    fun `친구 추가 요청 시 Conflict 에러가 발생하면 "이미 친구거나 이미 요청된 상태를 의미하는" 에러가 발생한다`() = runTest {
        // given
        every {
            friendRepository.addFriend(any())
        } returns flowOf(Result.Error(CommonErrorType.Network.Conflict))

        // when
        val currentFriendStatus = FriendStatus.NOTHING
        val result = usecase(TestReceiverId.value, currentFriendStatus).last()

        // then
        val error = result as Result.Error
        assertEquals(ProfileErrorType.AlreadyFriendsOrRequested, error.error)
    }

    @Test
    fun `친구 삭제 요청 시 NotFound 에러가 발생하면 "이미 처리된 요청임을 의미하는" 에러가 발생한다`() = runTest {
        // given
        every {
            friendRepository.deleteFriend(any())
        } returns flowOf(Result.Error(CommonErrorType.Network.NotFound))

        // when
        val currentFriendStatus = FriendStatus.FRIENDS
        val result = usecase(TestReceiverId.value, currentFriendStatus).last()

        // then
        val error = result as Result.Error
        assertEquals(ProfileErrorType.AlreadyProcessed, error.error)
    }

    @Test
    fun `친구 관계 상태 업데이트 요청 시 NotFound 에러가 발생하면 "이미 처리된 요청임을 의미하는" 에러가 발생한다`() = runTest {
        // given
        every {
            friendRepository.updateFriendStatus(any())
        } returns flowOf(Result.Error(CommonErrorType.Network.NotFound))

        // when
        val currentFriendStatus = FriendStatus.RECEIVED
        val result = usecase(TestReceiverId.value, currentFriendStatus).last()

        // then
        val error = result as Result.Error
        assertEquals(ProfileErrorType.AlreadyProcessed, error.error)
    }

    companion object {
        private val TestRequesterId = UserId(1L)
        private val TestReceiverId = UserId(2L)
        private val TestFriend = Friend(
            id = FriendId(1L),
            requesterId = TestRequesterId,
            receiverId = TestReceiverId,
            requestStatus = FriendRequestStatus.PENDING,
            respondedAt = 1000L,
            createdAt = 1000L,
            updatedAt = 1000L,
        )
    }
}
