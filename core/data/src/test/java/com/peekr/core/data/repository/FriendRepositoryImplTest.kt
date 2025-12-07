package com.peekr.core.data.repository

import com.peekr.core.data.source.network.datasource.FriendNetworkDataSource
import com.peekr.core.data.source.network.dto.friend.request.AddFriendRequest
import com.peekr.core.data.source.network.dto.friend.request.DeleteFriendRequest
import com.peekr.core.data.source.network.dto.friend.request.PatchFriendshipStatusRequest
import com.peekr.core.data.source.network.dto.friend.response.FriendResponse
import com.peekr.core.data.source.network.error.NetworkErrorType
import com.peekr.core.data.source.network.error.toCommonErrorType
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.friend.model.AddFriend
import com.peekr.core.domain.friend.model.DeleteFriend
import com.peekr.core.domain.friend.model.PatchFriendshipStatus
import com.peekr.core.domain.model.FriendshipStatus
import com.peekr.core.domain.model.UserId
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FriendRepositoryImplTest {
    private val dataSource: FriendNetworkDataSource = mockk()
    private val dispatcher = UnconfinedTestDispatcher()
    private val repository = FriendRepositoryImpl(dataSource, dispatcher)

    @Before
    fun setUp() {
        coEvery {
            dataSource.addFriend(TestAddFriendRequest)
        } returns NetworkResult.Success(TestFriendResponse)
        coEvery {
            dataSource.deleteFriend(TestDeleteFriendRequest)
        } returns NetworkResult.Success(Unit)
        coEvery {
            dataSource.updateFriendshipStatus(TestPatchFriendshipStatusRequest)
        } returns NetworkResult.Success(Unit)
    }

    @Test
    fun `친구 추가 - 성공 테스트`() = runTest {
        // when
        val result = repository.addFriend(TestAddFriend).last()

        // then
        val success = result as Result.Success
        assertEquals(TestFriendResponse.requesterId, success.data.requesterId.value)
        assertEquals(TestFriendResponse.receiverId, success.data.receiverId.value)
        assertEquals(TestFriendResponse.status, success.data.status)
    }

    @Test
    fun `친구 추가 - 알려진 에러 방출 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = NetworkErrorType.Unexpected(null)
        coEvery {
            dataSource.addFriend(any())
        } returns NetworkResult.Error(expectedError)

        // when
        val result = repository.addFriend(TestAddFriend).last()

        // then
        val error = result as Result.Error
        assertEquals(expectedError.toCommonErrorType(), error.error)
    }

    @Test
    fun `친구 추가 - 예외 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val exception = Exception("error!")
        coEvery {
            dataSource.addFriend(any())
        } throws exception

        // when
        val result = repository.addFriend(TestAddFriend).last()

        // then
        val error = result as Result.Error
        if (error.error is CommonErrorType.Unexpected) {
            assertEquals(
                CommonErrorType.Unexpected(exception).cause?.message,
                (error.error as CommonErrorType.Unexpected).cause?.message,
            )
        }
    }

    @Test
    fun `친구 추가 - HTTP 상태코드 409 에러가 발생한 경우 알려진 에러로 반환한다`() = runTest {
        // given
        val expectedError = NetworkErrorType.Network.HttpError(409)
        coEvery {
            dataSource.addFriend(any())
        } returns NetworkResult.Error(expectedError)

        // when
        val result = repository.addFriend(TestAddFriend).last()

        // then
        val error = result as Result.Error
        assertEquals(CommonErrorType.Network.Conflict, error.error)
    }

    @Test
    fun `친구 삭제 - 성공 테스트`() = runTest {
        // when
        val result = repository.deleteFriend(TestDeleteFriend).last()

        // then
        assertTrue(result is Result.Success)
    }

    @Test
    fun `친구 삭제 - 알려진 에러 방출 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = NetworkErrorType.Unexpected(null)
        coEvery {
            dataSource.deleteFriend(any())
        } returns NetworkResult.Error(expectedError)

        // when
        val result = repository.deleteFriend(TestDeleteFriend).last()

        // then
        val error = result as Result.Error
        assertEquals(expectedError.toCommonErrorType(), error.error)
    }

    @Test
    fun `친구 삭제 - 예외 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val exception = Exception("error!")
        coEvery {
            dataSource.deleteFriend(any())
        } throws exception

        // when
        val result = repository.deleteFriend(TestDeleteFriend).last()

        // then
        val error = result as Result.Error
        if (error.error is CommonErrorType.Unexpected) {
            assertEquals(
                CommonErrorType.Unexpected(exception).cause?.message,
                (error.error as CommonErrorType.Unexpected).cause?.message,
            )
        }
    }

    @Test
    fun `친구 삭제 - HTTP 상태코드 404 에러가 발생한 경우 알려진 에러로 반환한다`() = runTest {
        // given
        val expectedError = NetworkErrorType.Network.HttpError(404)
        coEvery {
            dataSource.deleteFriend(any())
        } returns NetworkResult.Error(expectedError)

        // when
        val result = repository.deleteFriend(TestDeleteFriend).last()

        // then
        val error = result as Result.Error
        assertEquals(CommonErrorType.Network.NotFound, error.error)
    }

    @Test
    fun `친구 관계 상태 수정 - 성공 테스트`() = runTest {
        // when
        val result = repository.updateFriendshipStatus(TestPatchFriendshipStatus).last()

        // then
        assertTrue(result is Result.Success)
    }

    @Test
    fun `친구 관계 상태 수정 - 알려진 에러 방출 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = NetworkErrorType.Unexpected(null)
        coEvery {
            dataSource.updateFriendshipStatus(any())
        } returns NetworkResult.Error(expectedError)

        // when
        val result = repository.updateFriendshipStatus(TestPatchFriendshipStatus).last()

        // then
        val error = result as Result.Error
        assertEquals(expectedError.toCommonErrorType(), error.error)
    }

    @Test
    fun `친구 관계 상태 수정 - 예외 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val exception = Exception("error!")
        coEvery {
            dataSource.updateFriendshipStatus(any())
        } throws exception

        // when
        val result = repository.updateFriendshipStatus(TestPatchFriendshipStatus).last()

        // then
        val error = result as Result.Error
        if (error.error is CommonErrorType.Unexpected) {
            assertEquals(
                CommonErrorType.Unexpected(exception).cause?.message,
                (error.error as CommonErrorType.Unexpected).cause?.message,
            )
        }
    }

    @Test
    fun `친구 관계 상태 수정 - HTTP 상태코드 404 에러가 발생한 경우 알려진 에러로 반환한다`() = runTest {
        // given
        val expectedError = NetworkErrorType.Network.HttpError(404)
        coEvery {
            dataSource.updateFriendshipStatus(any())
        } returns NetworkResult.Error(expectedError)

        // when
        val result = repository.updateFriendshipStatus(TestPatchFriendshipStatus).last()

        // then
        val error = result as Result.Error
        assertEquals(CommonErrorType.Network.NotFound, error.error)
    }

    companion object {
        private val TestRequesterId = UserId(1L)
        private val TestReceiverId = UserId(2L)
        private val TestFriendResponse = FriendResponse(
            id = 1L,
            requesterId = TestRequesterId.value,
            receiverId = TestReceiverId.value,
            status = FriendshipStatus.NOTHING,
            respondedAt = 1000L,
            createdAt = 1000L,
            updatedAt = 1000L,
        )
        private val TestAddFriendRequest = AddFriendRequest(
            requesterId = TestRequesterId.value,
            receiverId = TestReceiverId.value,
        )
        private val TestDeleteFriendRequest = DeleteFriendRequest(
            requesterId = TestRequesterId.value,
            receiverId = TestReceiverId.value,
        )
        private val TestPatchFriendshipStatusRequest = PatchFriendshipStatusRequest(
            requesterId = TestRequesterId.value,
            receiverId = TestReceiverId.value,
            status = FriendshipStatus.NOTHING,
        )
        private val TestAddFriend = AddFriend(
            requesterId = TestRequesterId,
            receiverId = TestReceiverId,
        )
        private val TestDeleteFriend = DeleteFriend(
            requesterId = TestRequesterId,
            receiverId = TestReceiverId,
        )
        private val TestPatchFriendshipStatus = PatchFriendshipStatus(
            requesterId = TestRequesterId,
            receiverId = TestReceiverId,
            status = FriendshipStatus.NOTHING,
        )
    }
}
