package com.turnin.domain.profile.usecase.user

import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.CommonErrorType
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
        every { friendRepository.addFriend(any()) } returns flowOf(Result.Success(Unit))
        every { friendRepository.deleteFriend(any()) } returns flowOf(Result.Success(Unit))
        every { friendRepository.updateFriendStatus(any()) } returns flowOf(Result.Success(Unit))
    }

    // ------------------------------ 정상 처리 ------------------------------

    @Test
    fun `NOTHING 상태에서 친구 추가 요청 시 정상적으로 처리되고 REQUESTED 상태를 반환한다`() = runTest {
        // when
        val currentFriendStatus = FriendStatus.NOTHING
        val result = usecase(TestReceiverId.value, currentFriendStatus).last()

        // then
        val success = result as Result.Success
        assertEquals(FriendStatus.REQUESTED, currentFriendStatus.toggle())
        assertEquals(FriendStatus.REQUESTED, success.data)
    }

    @Test
    fun `FRIENDS 상태에서 친구 삭제 요청 시 정상적으로 처리되고 NOTHING 상태를 반환한다`() = runTest {
        // when
        val currentFriendStatus = FriendStatus.FRIENDS
        val result = usecase(TestReceiverId.value, currentFriendStatus).last()

        // then
        val success = result as Result.Success
        assertEquals(FriendStatus.NOTHING, currentFriendStatus.toggle())
        assertEquals(FriendStatus.NOTHING, success.data)
    }

    @Test
    fun `REQUESTED 상태에서 친구 요청 취소 시 정상적으로 처리되고 NOTHING 상태를 반환한다`() = runTest {
        // when
        val currentFriendStatus = FriendStatus.REQUESTED
        val result = usecase(TestReceiverId.value, currentFriendStatus).last()

        // then
        val success = result as Result.Success
        assertEquals(FriendStatus.NOTHING, currentFriendStatus.toggle())
        assertEquals(FriendStatus.NOTHING, success.data)
    }

    @Test
    fun `RECEIVED 상태에서 친구 요청 수락 시 정상적으로 처리되고 FRIENDS 상태를 반환한다`() = runTest {
        // when
        val currentFriendStatus = FriendStatus.RECEIVED
        val result = usecase(TestReceiverId.value, currentFriendStatus).last()

        // then
        val success = result as Result.Success
        assertEquals(FriendStatus.FRIENDS, currentFriendStatus.toggle())
        assertEquals(FriendStatus.FRIENDS, success.data)
    }

    // ------------------------------ 사용자 ID 로드 실패 ------------------------------

    @Test
    fun `사용자 ID를 찾지 못하는 경우 MyUserIdNotFound 에러를 반환한다`() = runTest {
        // given: getMyUserId()가 null을 반환
        coEvery { userRepository.getMyUserId() } returns null

        // when
        val result = usecase(TestReceiverId.value, FriendStatus.NOTHING).last()

        // then
        val error = result as Result.Error
        assertEquals(ProfileErrorType.MyUserIdNotFound, error.error)
    }

    // ------------------------------ addFriend 에러 처리 (NOTHING) ------------------------------

    @Test
    fun `NOTHING 상태에서 친구 추가 요청 시 Conflict 에러가 발생하면 AlreadyFriendsOrRequested 에러를 반환한다`() = runTest {
        // given: 이미 친구이거나 요청된 상태
        every {
            friendRepository.addFriend(any())
        } returns flowOf(Result.Error(CommonErrorType.Network.Conflict))

        // when
        val result = usecase(TestReceiverId.value, FriendStatus.NOTHING).last()

        // then
        val error = result as Result.Error
        assertEquals(ProfileErrorType.AlreadyFriendsOrRequested, error.error)
    }

    @Test
    fun `NOTHING 상태에서 친구 추가 요청 시 NotFound 에러가 발생하면 FriendNotFound 에러를 반환한다`() = runTest {
        // given: 대상 사용자를 찾을 수 없는 상태
        every {
            friendRepository.addFriend(any())
        } returns flowOf(Result.Error(CommonErrorType.Network.NotFound))

        // when
        val result = usecase(TestReceiverId.value, FriendStatus.NOTHING).last()

        // then
        val error = result as Result.Error
        assertEquals(ProfileErrorType.FriendNotFound, error.error)
    }

    @Test
    fun `NOTHING 상태에서 친구 추가 요청 시 그 외 에러가 발생하면 CommonError로 래핑하여 반환한다`() = runTest {
        // given: 그 외 에러 상황
        val commonError = CommonErrorType.Network.InternalServerError
        every {
            friendRepository.addFriend(any())
        } returns flowOf(Result.Error(commonError))

        // when
        val result = usecase(TestReceiverId.value, FriendStatus.NOTHING).last()

        // then
        val error = result as Result.Error
        assertEquals(ProfileErrorType.CommonError(commonError), error.error)
    }

    // ------------------------------ deleteFriend 에러 처리 (FRIENDS, REQUESTED) ------------------------------

    @Test
    fun `FRIENDS 상태에서 친구 삭제 요청 시 NotFound 에러가 발생하면 AlreadyProcessed 에러를 반환한다`() = runTest {
        // given: 이미 삭제된 친구 관계
        every {
            friendRepository.deleteFriend(any())
        } returns flowOf(Result.Error(CommonErrorType.Network.NotFound))

        // when
        val result = usecase(TestReceiverId.value, FriendStatus.FRIENDS).last()

        // then
        val error = result as Result.Error
        assertEquals(ProfileErrorType.AlreadyProcessed, error.error)
    }

    @Test
    fun `REQUESTED 상태에서 친구 요청 취소 시 NotFound 에러가 발생하면 AlreadyProcessed 에러를 반환한다`() = runTest {
        // given: 이미 취소된 친구 요청
        every {
            friendRepository.deleteFriend(any())
        } returns flowOf(Result.Error(CommonErrorType.Network.NotFound))

        // when
        val result = usecase(TestReceiverId.value, FriendStatus.REQUESTED).last()

        // then
        val error = result as Result.Error
        assertEquals(ProfileErrorType.AlreadyProcessed, error.error)
    }

    @Test
    fun `FRIENDS 상태에서 친구 삭제 요청 시 그 외 에러가 발생하면 CommonError로 래핑하여 반환한다`() = runTest {
        // given
        val commonError = CommonErrorType.Network.InternalServerError
        every {
            friendRepository.deleteFriend(any())
        } returns flowOf(Result.Error(commonError))

        // when
        val result = usecase(TestReceiverId.value, FriendStatus.FRIENDS).last()

        // then
        val error = result as Result.Error
        assertEquals(ProfileErrorType.CommonError(commonError), error.error)
    }

    // ------------------------------ updateFriendStatus 에러 처리 (RECEIVED) ------------------------------

    @Test
    fun `RECEIVED 상태에서 친구 요청 수락 시 Conflict 에러가 발생하면 AlreadyProcessed 에러를 반환한다`() = runTest {
        // given: 이미 처리된 친구 요청
        every {
            friendRepository.updateFriendStatus(any())
        } returns flowOf(Result.Error(CommonErrorType.Network.Conflict))

        // when
        val result = usecase(TestReceiverId.value, FriendStatus.RECEIVED).last()

        // then
        val error = result as Result.Error
        assertEquals(ProfileErrorType.AlreadyProcessed, error.error)
    }

    @Test
    fun `RECEIVED 상태에서 친구 요청 수락 시 NotFound 에러가 발생하면 FriendNotFound 에러를 반환한다`() = runTest {
        // given: 대상 친구 요청을 찾을 수 없는 상태
        every {
            friendRepository.updateFriendStatus(any())
        } returns flowOf(Result.Error(CommonErrorType.Network.NotFound))

        // when
        val result = usecase(TestReceiverId.value, FriendStatus.RECEIVED).last()

        // then
        val error = result as Result.Error
        assertEquals(ProfileErrorType.FriendNotFound, error.error)
    }

    @Test
    fun `RECEIVED 상태에서 친구 요청 수락 시 그 외 에러가 발생하면 CommonError로 래핑하여 반환한다`() = runTest {
        // given
        val commonError = CommonErrorType.Network.InternalServerError
        every {
            friendRepository.updateFriendStatus(any())
        } returns flowOf(Result.Error(commonError))

        // when
        val result = usecase(TestReceiverId.value, FriendStatus.RECEIVED).last()

        // then
        val error = result as Result.Error
        assertEquals(ProfileErrorType.CommonError(commonError), error.error)
    }

    companion object {
        private val TestRequesterId = UserId(1L)
        private val TestReceiverId = UserId(2L)
    }
}
