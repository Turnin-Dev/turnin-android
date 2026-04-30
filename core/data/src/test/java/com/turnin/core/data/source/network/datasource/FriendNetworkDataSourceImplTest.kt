package com.turnin.core.data.source.network.datasource

import com.turnin.core.data.ServerTestRule
import com.turnin.core.data.source.network.api.FriendApi
import com.turnin.core.data.source.network.dto.friend.request.AddFriendRequest
import com.turnin.core.data.source.network.dto.friend.request.DeleteFriendRequest
import com.turnin.core.data.source.network.dto.friend.request.PatchFriendStatusRequest
import com.turnin.core.data.source.network.dto.friend.response.FriendInfoResponse
import com.turnin.core.data.source.network.dto.friend.response.FriendResponse
import com.turnin.core.data.source.network.dto.friend.response.FriendsResponse
import com.turnin.core.data.source.network.error.NetworkErrorType
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.domain.friend.model.FriendRequestStatus
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FriendNetworkDataSourceImplTest {
    @get:Rule
    val testRule = ServerTestRule()

    private val friendApi: FriendApi
        get() = testRule.createNetworkApi<FriendApi>(testRule.moshi)

    private lateinit var dataSource: FriendNetworkDataSource

    @Before
    fun setUp() {
        dataSource = FriendNetworkDataSourceImpl(friendApi)
    }

    @Test
    fun `친구 목록 조회 - 성공 테스트`() = runTest {
        // given
        val testFriendsResponseJson = testRule.encodeToJson(TestFriendsResponse)
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(testFriendsResponseJson)
            },
        )

        // when
        val response = dataSource.getFriends(1L, 1, 10)

        // then
        val success = response as NetworkResult.Success
        assertEquals(TestFriendsResponse, success.data)
    }

    @Test
    fun `친구 목록 조회 - 잘못된 응답 바디로 응답 시 알려진 에러를 반환한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(TestInvalidResponse)
            },
        )

        // when
        val response = dataSource.getFriends(1L, 1, 10)

        // then
        val error = response as NetworkResult.Error
        assertEquals(NetworkErrorType.Exception.JsonData, error.error)
    }

    @Test
    fun `친구 목록 조회 - 알 수 없는 예외 발생 시 Unexpected 에러를 반환한다`() = runTest {
        // given
        val mockApi: FriendApi = mockk()
        val exception = Exception()
        dataSource = FriendNetworkDataSourceImpl(mockApi)
        coEvery { mockApi.getFriends(any(), any(), any()) } throws exception

        // when
        val response = dataSource.getFriends(1L, 1, 10)

        // then
        val error = response as NetworkResult.Error
        assertEquals(NetworkErrorType.Unexpected(exception), error.error)
    }

    @Test
    fun `친구 목록 조회 - HTTP 상태코드 404 응답 시 NotFound 에러로 반환한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(404)
            },
        )

        // when
        val response = dataSource.getFriends(1L, 1, 10)

        // then
        val error = response as NetworkResult.Error
        assertEquals(404, error.status)
    }

    @Test
    fun `친구 추가 - 성공 테스트`() = runTest {
        // given
        val testFriendResponseJson = testRule.encodeToJson(TestFriendResponse)
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(201)
                setBody(testFriendResponseJson)
            },
        )

        // when
        val response = dataSource.addFriend(TestAddFriendRequest)

        // then
        val success = response as NetworkResult.Success
        assertEquals(TestFriendResponse.requesterId, success.data.requesterId)
        assertEquals(TestFriendResponse.receiverId, success.data.receiverId)
        assertEquals(TestFriendResponse.requestState, success.data.requestState)
    }

    @Test
    fun `친구 추가 - 잘못된 응답 바디로 응답 시 알려진 에러를 반환한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(TestInvalidResponse)
            },
        )

        // when
        val response = dataSource.addFriend(TestAddFriendRequest)

        // then
        val error = response as NetworkResult.Error
        assertEquals(NetworkErrorType.Exception.JsonData, error.error)
    }

    @Test
    fun `친구 추가 - 알 수 없는 예외 발생 시 Unexpected 에러를 반환한다`() = runTest {
        // given
        val mockApi: FriendApi = mockk()
        val exception = Exception()
        dataSource = FriendNetworkDataSourceImpl(mockApi)
        coEvery { mockApi.addFriend(any()) } throws exception

        // when
        val response = dataSource.addFriend(TestAddFriendRequest)

        // then
        val error = response as NetworkResult.Error
        assertEquals(NetworkErrorType.Unexpected(exception), error.error)
    }

    @Test
    fun `친구 추가 - HTTP 상태코드 404 응답 시 NotFound 에러로 반환한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(404)
            },
        )

        // when
        val response = dataSource.addFriend(TestAddFriendRequest)

        // then
        val error = response as NetworkResult.Error
        assertEquals(404, error.status)
    }

    @Test
    fun `친구 삭제 - 성공 테스트`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(201)
            },
        )

        // when
        val response = dataSource.deleteFriend(TestDeleteFriendRequest)

        // then
        assertTrue(response is NetworkResult.Success)
    }

    @Test
    fun `친구 삭제 - 알 수 없는 예외 발생 시 Unexpected 에러를 반환한다`() = runTest {
        // given
        val mockApi: FriendApi = mockk()
        val exception = Exception()
        dataSource = FriendNetworkDataSourceImpl(mockApi)
        coEvery { mockApi.deleteFriend(any(), any()) } throws exception

        // when
        val response = dataSource.deleteFriend(TestDeleteFriendRequest)

        // then
        val error = response as NetworkResult.Error
        assertEquals(NetworkErrorType.Unexpected(exception), error.error)
    }

    @Test
    fun `친구 삭제 - HTTP 상태코드 404 응답 시 NotFound 에러로 반환한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(404)
            },
        )

        // when
        val response = dataSource.deleteFriend(TestDeleteFriendRequest)

        // then
        val error = response as NetworkResult.Error
        assertEquals(404, error.status)
    }

    @Test
    fun `친구 관계 상태 수정 - 성공 테스트`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(201)
            },
        )

        // when
        val response = dataSource.updateFriendStatus(TestPatchFriendRequestStatusRequest)

        // then
        assertTrue(response is NetworkResult.Success)
    }

    @Test
    fun `친구 관계 상태 수정 - 알 수 없는 예외 발생 시 Unexpected 에러를 반환한다`() = runTest {
        // given
        val mockApi: FriendApi = mockk()
        val exception = Exception()
        dataSource = FriendNetworkDataSourceImpl(mockApi)
        coEvery { mockApi.updateFriendStatus(any()) } throws exception

        // when
        val response = dataSource.updateFriendStatus(TestPatchFriendRequestStatusRequest)

        // then
        val error = response as NetworkResult.Error
        assertEquals(NetworkErrorType.Unexpected(exception), error.error)
    }

    @Test
    fun `친구 관계 상태 수정 - HTTP 상태코드 404 응답 시 NotFound 에러로 반환한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(404)
            },
        )

        // when
        val response = dataSource.updateFriendStatus(TestPatchFriendRequestStatusRequest)

        // then
        val error = response as NetworkResult.Error
        assertEquals(404, error.status)
    }

    companion object {
        private const val TEST_REQUESTER_ID = 1L
        private const val TEST_RECEIVER_ID = 2L
        private val TestFriendResponse = FriendResponse(
            id = 1L,
            requesterId = TEST_REQUESTER_ID,
            receiverId = TEST_RECEIVER_ID,
            requestState = FriendRequestStatus.PENDING,
            respondedAt = 1000L,
            createdAt = 1000L,
            updatedAt = 1000L,
        )
        private val TestAddFriendRequest = AddFriendRequest(
            requesterId = TEST_REQUESTER_ID,
            receiverId = TEST_RECEIVER_ID,
        )
        private val TestDeleteFriendRequest = DeleteFriendRequest(
            requesterId = TEST_REQUESTER_ID,
            receiverId = TEST_RECEIVER_ID,
        )
        private val TestPatchFriendRequestStatusRequest = PatchFriendStatusRequest(
            requesterId = TEST_REQUESTER_ID,
            receiverId = TEST_RECEIVER_ID,
            requestStatus = FriendRequestStatus.PENDING,
        )
        private val TestInvalidResponse =
            """
            {
                "what": "???"
            }
            """.trimIndent()

        private val TestFriendInfoResponse = FriendInfoResponse(
            id = 1L,
            userId = TEST_RECEIVER_ID,
            displayId = "did",
            name = "name",
            profileImageUrl = null,
            respondedAt = 1000,
            createdAt = 1000,
            updatedAt = 1000,
        )

        private val TestFriendsResponse = FriendsResponse(
            pageNumber = 1,
            pageSize = 1,
            totalSize = 100,
            hasNext = true,
            list = listOf(TestFriendInfoResponse),
        )
    }
}
