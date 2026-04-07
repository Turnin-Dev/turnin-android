package com.peekr.core.data.repository

import androidx.paging.testing.asSnapshot
import com.peekr.core.common.logger.AppLogger
import com.peekr.core.data.MockLog
import com.peekr.core.data.source.local.memory.MemoryCache
import com.peekr.core.data.source.network.datasource.FriendNetworkDataSource
import com.peekr.core.data.source.network.dto.friend.request.AddFriendRequest
import com.peekr.core.data.source.network.dto.friend.request.DeleteFriendRequest
import com.peekr.core.data.source.network.dto.friend.request.PatchFriendStatusRequest
import com.peekr.core.data.source.network.dto.friend.response.FriendInfoResponse
import com.peekr.core.data.source.network.dto.friend.response.FriendResponse
import com.peekr.core.data.source.network.dto.friend.response.FriendsResponse
import com.peekr.core.data.source.network.dto.friend.response.IncomingRequestResponse
import com.peekr.core.data.source.network.dto.friend.response.IncomingRequestsResponse
import com.peekr.core.data.source.network.dto.friend.response.toDomainModel
import com.peekr.core.data.source.network.error.NetworkErrorType
import com.peekr.core.data.source.network.error.toCommonErrorType
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.friend.model.AddFriend
import com.peekr.core.domain.friend.model.DeleteFriend
import com.peekr.core.domain.friend.model.FriendPagingTokens
import com.peekr.core.domain.friend.model.FriendRequestStatus
import com.peekr.core.domain.friend.model.IncomingRequestPagingTokens
import com.peekr.core.domain.friend.model.PatchFriendStatus
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.user.model.CoreUserProfile
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Friend 리포지토리 + 페이징 테스트가 포함
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FriendRepositoryImplTest {
    private val dataSource: FriendNetworkDataSource = mockk()
    private val memoryCache: MemoryCache<UserId, CoreUserProfile> = mockk()
    private val dispatcher = UnconfinedTestDispatcher()
    private val repository = FriendRepositoryImpl(dataSource, memoryCache, dispatcher)

    @Before
    fun setUp() {
        every { memoryCache.remove(any()) } returns null
        coEvery {
            dataSource.addFriend(TestAddFriendRequest)
        } returns NetworkResult.Success(TestFriendResponse)
        coEvery {
            dataSource.deleteFriend(TestDeleteFriendRequest)
        } returns NetworkResult.Success(Unit)
        coEvery {
            dataSource.updateFriendStatus(TestPatchFriendRequestStatusRequest)
        } returns NetworkResult.Success(Unit)

        mockkObject(AppLogger)
        every { AppLogger.d(any(), any()) } just Runs
        every { AppLogger.d(any(), any(), any()) } just Runs
        // Paging 라이브러리 내부에서 발생하는 Log 호출 방지
        MockLog.mock()
    }

    @After
    fun tearDown() {
        clearAllMocks()
        unmockkObject(AppLogger::class)
        MockLog.cleanUp()
    }

    /**
     * 해당 테스트는 prefetchDistance가 pageSize보다 작다는 가정 하에 진행된다.
     */
    @Test
    fun `친구 목록 조회 - 초기 호출 성공 시 도메인 모델로 변환된 데이터를 반환한다`() = runTest {
        // given
        val pageSize = FriendPagingTokens.PAGE_SIZE
        val expectedFirstPage = createFriendInfoResponseList(1, pageSize).map { it.toDomainModel() }

        // 첫 번째 페이지 설정 (page=1, size=20)
        coEvery {
            dataSource.getFriends(1L, 1, pageSize)
        } returns NetworkResult.Success(
            createFriendsResponse(
                pageNumber = 1L,
                startId = 1L,
                count = pageSize,
                hasNext = true,
            ),
        )

        // 두 번째 페이지 설정 (page=2, size=20)
        // Paging Source는 initialLoadSize(30)를 채우기 위해 2페이지를 요청할 것으로 예상
        coEvery {
            dataSource.getFriends(1L, 2, pageSize)
        } returns NetworkResult.Success(
            createFriendsResponse(
                pageNumber = 2L,
                startId = (pageSize + 1).toLong(),
                count = pageSize,
                hasNext = true,
            ),
        )

        // when
        val pagingData = repository.getFriends(UserId(1L)).asSnapshot()

        // then
        assertEquals(pageSize * 2, pagingData.size)
        assertEquals(expectedFirstPage.first().id, pagingData.first().id)
        val expectedLastId = pageSize * 2
        assertEquals(expectedLastId.toLong(), pagingData.last().id.value)
    }

    @Test
    fun `나에게 들어온 친구 요청 목록 조회 - 초기 호출 성공 시 도메인 모델로 변환된 데이터를 반환한다`() = runTest {
        // given
        val pageSize = IncomingRequestPagingTokens.PAGE_SIZE
        val expectedFirstPage =
            createIncomingRequestResponseList(startId = 1, count = pageSize)
                .map { it.toDomainModel() }

        // 첫 번째 페이지 설정 (page=1, size=20)
        coEvery {
            dataSource.getIncomingRequests(1, pageSize)
        } returns NetworkResult.Success(
            createIncomingRequestsResponse(
                pageNumber = 1L,
                startId = 1L,
                count = pageSize,
                hasNext = true,
            ),
        )

        // 두 번째 페이지 설정 (page=2, size=20)
        // Paging Source는 initialLoadSize(30)를 채우기 위해 2페이지를 요청할 것으로 예상
        coEvery {
            dataSource.getIncomingRequests(2, pageSize)
        } returns NetworkResult.Success(
            createIncomingRequestsResponse(
                pageNumber = 2L,
                startId = pageSize + 1L,
                count = pageSize,
                hasNext = true,
            ),
        )

        // when
        val pagingData = repository.getIncomingRequests().asSnapshot()

        // then
        assertEquals(pageSize * 2, pagingData.size)
        assertEquals(expectedFirstPage.first().id, pagingData.first().id)
        val expectedLastId = pageSize * 2
        assertEquals(expectedLastId.toLong(), pagingData.last().id.value)
    }

    @Test
    fun `친구 추가 - 성공 테스트`() = runTest {
        // when
        val result = repository.addFriend(TestAddFriend).last()

        // then
        val success = result as Result.Success
        assertEquals(TestFriendResponse.requesterId, success.data.requesterId.value)
        assertEquals(TestFriendResponse.receiverId, success.data.receiverId.value)
        assertEquals(TestFriendResponse.requestState, success.data.requestStatus)
        verify(exactly = 1) { memoryCache.remove(TestAddFriend.receiverId) }
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
        verify(exactly = 1) { memoryCache.remove(TestDeleteFriend.receiverId) }
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
        val result = repository.updateFriendStatus(TestPatchFriendRequestStatus).last()

        // then
        assertTrue(result is Result.Success)
        verify(exactly = 1) { memoryCache.remove(TestPatchFriendRequestStatus.receiverId) }
    }

    @Test
    fun `친구 관계 상태 수정 - 알려진 에러 방출 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = NetworkErrorType.Unexpected(null)
        coEvery {
            dataSource.updateFriendStatus(any())
        } returns NetworkResult.Error(expectedError)

        // when
        val result = repository.updateFriendStatus(TestPatchFriendRequestStatus).last()

        // then
        val error = result as Result.Error
        assertEquals(expectedError.toCommonErrorType(), error.error)
    }

    @Test
    fun `친구 관계 상태 수정 - 예외 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val exception = Exception("error!")
        coEvery {
            dataSource.updateFriendStatus(any())
        } throws exception

        // when
        val result = repository.updateFriendStatus(TestPatchFriendRequestStatus).last()

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
            dataSource.updateFriendStatus(any())
        } returns NetworkResult.Error(expectedError)

        // when
        val result = repository.updateFriendStatus(TestPatchFriendRequestStatus).last()

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
            requestState = FriendRequestStatus.PENDING,
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
        private val TestPatchFriendRequestStatusRequest = PatchFriendStatusRequest(
            requesterId = TestRequesterId.value,
            receiverId = TestReceiverId.value,
            requestStatus = FriendRequestStatus.PENDING,
        )
        private val TestAddFriend = AddFriend(
            requesterId = TestRequesterId,
            receiverId = TestReceiverId,
        )
        private val TestDeleteFriend = DeleteFriend(
            requesterId = TestRequesterId,
            receiverId = TestReceiverId,
        )
        private val TestPatchFriendRequestStatus = PatchFriendStatus(
            requesterId = TestRequesterId,
            receiverId = TestReceiverId,
            requestStatus = FriendRequestStatus.PENDING,
        )
    }

    private fun createFriendsResponse(
        pageNumber: Long,
        startId: Long,
        count: Int,
        hasNext: Boolean,
    ): FriendsResponse = FriendsResponse(
        pageNumber = pageNumber,
        pageSize = count,
        totalSize = 100L,
        hasNext = hasNext,
        list = createFriendInfoResponseList(startId, count),
    )

    private fun createFriendInfoResponseList(
        startId: Long,
        count: Int,
    ): List<FriendInfoResponse> =
        (startId until startId + count).map { id ->
            FriendInfoResponse(
                id = id,
                userId = id,
                displayId = "did",
                name = "name",
                profileImageUrl = null,
                respondedAt = 1000,
                createdAt = 1000,
                updatedAt = 1000,
            )
        }

    private fun createIncomingRequestsResponse(
        pageNumber: Long,
        startId: Long,
        count: Int,
        hasNext: Boolean,
    ): IncomingRequestsResponse = IncomingRequestsResponse(
        pageNumber = pageNumber,
        pageSize = count,
        totalSize = 100L,
        hasNext = hasNext,
        list = createIncomingRequestResponseList(startId, count),
    )

    private fun createIncomingRequestResponseList(
        startId: Long,
        count: Int,
    ): List<IncomingRequestResponse> =
        (startId until startId + count).map { id ->
            IncomingRequestResponse(
                id = id,
                userId = id + 1L,
                displayId = "did${id + 1L}",
                name = "name${id + 1L}",
                profileImageUrl = null,
                respondedAt = 1000,
                createdAt = 1000,
                updatedAt = 1000,
            )
        }
}
