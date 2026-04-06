package com.peekr.domain.friend.usecase

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.friend.model.FriendRequestStatus
import com.peekr.core.domain.friend.model.PatchFriendStatus
import com.peekr.core.domain.friend.repository.FriendRepository
import com.peekr.core.domain.model.UserId
import com.peekr.domain.friend.error.FriendErrorType
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AcceptFriendRequestUseCaseTest {
    private val friendRepository: FriendRepository = mockk()
    private val useCase = AcceptFriendRequestUseCase(friendRepository)

    private val myUserId = 1L
    private val targetUserId = 2L
    private val patch = PatchFriendStatus(
        requesterId = UserId(myUserId),
        receiverId = UserId(targetUserId),
        requestStatus = FriendRequestStatus.ACCEPTED,
    )

    @Test
    fun `친구 요청 수락 성공 시 Success(Unit)을 반환한다`() = runTest {
        // given
        every { friendRepository.updateFriendStatus(patch) } returns flowOf(Result.Success(Unit))

        // when
        val results = useCase(myUserId, targetUserId).toList()

        // then
        assertEquals(listOf(Result.Success(Unit)), results)
    }

    @Test
    fun `친구 요청 수락 시 Forbidden 에러가 발생하면 NotSameRequesterIdAndMyId를 반환한다`() = runTest {
        // given
        val commonError = CommonErrorType.Network.Forbidden
        every { friendRepository.updateFriendStatus(patch) } returns flowOf(Result.Error(commonError))

        // when
        val results = useCase(myUserId, targetUserId).toList()

        // then
        assertEquals(listOf(Result.Error(FriendErrorType.NotSameRequesterIdAndMyId)), results)
    }

    @Test
    fun `친구 요청 수락 시 NotFound 에러가 발생하면 AlreadyProceedOrUserNotFound를 반환한다`() = runTest {
        // given
        val commonError = CommonErrorType.Network.NotFound
        every { friendRepository.updateFriendStatus(patch) } returns flowOf(Result.Error(commonError))

        // when
        val results = useCase(myUserId, targetUserId).toList()

        // then
        assertEquals(listOf(Result.Error(FriendErrorType.AlreadyProceedOrUserNotFound)), results)
    }

    @Test
    fun `친구 요청 수락 시 기타 에러가 발생하면 CommonError를 반환한다`() = runTest {
        // given
        val commonError = CommonErrorType.Unexpected(null)
        every { friendRepository.updateFriendStatus(patch) } returns flowOf(Result.Error(commonError))

        // when
        val results = useCase(myUserId, targetUserId).toList()

        // then
        assertEquals(listOf(Result.Error(FriendErrorType.CommonError(commonError))), results)
    }
}
