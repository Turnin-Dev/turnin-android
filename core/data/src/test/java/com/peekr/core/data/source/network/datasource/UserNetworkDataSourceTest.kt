package com.peekr.core.data.source.network.datasource

import com.peekr.core.data.ServerTestRule
import com.peekr.core.data.source.network.api.UserApi
import com.peekr.core.data.source.network.dto.user.request.IntroducePatchRequest
import com.peekr.core.data.source.network.dto.user.request.UserPatchRequest
import com.peekr.core.data.source.network.dto.user.response.MyProfileResponse
import com.peekr.core.data.source.network.dto.user.response.UserProfileResponse
import com.peekr.core.data.source.network.dto.user.response.UserResponse
import com.peekr.core.data.source.network.error.NetworkErrorType
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.domain.friend.model.FriendStatus
import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.Role
import com.peekr.core.domain.model.SocialLoginProvider
import com.peekr.core.domain.model.UserId
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UserNetworkDataSourceTest {
    @get:Rule
    val testRule = ServerTestRule()

    private val userApi: UserApi
        get() = testRule.createNetworkApi<UserApi>(testRule.moshi)

    private lateinit var dataSource: UserNetworkDataSource

    @Before
    fun setUp() {
        dataSource = UserNetworkDataSourceImpl(userApi)
    }

    @Test
    fun `사용자 조회 - 성공 테스트`() = runTest {
        // given
        val testUserResponseJson = testRule.encodeToJson(TestUserResponse)
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(testUserResponseJson)
            },
        )

        // when
        val response = dataSource.getUser()

        // then
        Assert.assertTrue(response is NetworkResult.Success)
        Assert.assertEquals(TestUserResponse.id, (response as NetworkResult.Success).data.id)
        Assert.assertEquals(TestUserResponse.displayId, response.data.displayId)
    }

    @Test
    fun `사용자 조회 - 잘못된 응답 바디로 응답 시 알려진 에러를 반환한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(TestInvalidResponse)
            },
        )

        // when
        val response = dataSource.getUser()

        // then
        Assert.assertTrue(response is NetworkResult.Error)
        Assert.assertEquals(
            NetworkErrorType.Exception.JsonData,
            (response as NetworkResult.Error).error,
        )
    }

    @Test
    fun `사용자 조회 - 알 수 없는 예외 발생 시 Unexpected 에러를 반환한다`() = runTest {
        // given
        val mockApi: UserApi = mockk()
        val exception = Exception()
        dataSource = UserNetworkDataSourceImpl(mockApi)
        coEvery { mockApi.getUser() } throws exception

        // when
        val response = dataSource.getUser()

        // then
        Assert.assertTrue(response is NetworkResult.Error)
        Assert.assertEquals(
            NetworkErrorType.Unexpected(exception),
            (response as NetworkResult.Error).error,
        )
    }

    @Test
    fun `사용자 조회 - HTTP 상태코드 404 응답 시 NotFound 에러로 반환한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(404)
            },
        )

        // when
        val response = dataSource.getUser()

        // then
        assertTrue(response is NetworkResult.Error)
        val error = (response as NetworkResult.Error).error as NetworkErrorType.Network.HttpError
        assertEquals(404, error.status)
    }

    @Test
    fun `나의 프로필 조회 - 성공 테스트`() = runTest {
        // given
        val testMyProfileResponseJson = testRule.encodeToJson(TestMyProfileResponse)
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(testMyProfileResponseJson)
            },
        )

        // when
        val response = dataSource.getMyProfile()

        // then
        val successResponse = response as NetworkResult.Success
        assertEquals(TestMyProfileResponse.displayId, successResponse.data.displayId)
        assertEquals(TestMyProfileResponse.name, successResponse.data.name)
    }

    @Test
    fun `나의 프로필 조회 - 잘못된 응답 바디로 응답 시 알려진 에러를 반환한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(TestInvalidResponse)
            },
        )

        // when
        val response = dataSource.getMyProfile()

        // then
        val errorResponse = response as NetworkResult.Error
        assertEquals(NetworkErrorType.Exception.JsonData, errorResponse.error)
    }

    @Test
    fun `나의 프로필 조회 - 알 수 없는 예외 발생 시 Unexpected 에러를 반환한다`() = runTest {
        // given
        val mockApi: UserApi = mockk()
        val exception = Exception()
        dataSource = UserNetworkDataSourceImpl(mockApi)
        coEvery { mockApi.getMyProfile() } throws exception

        // when
        val response = dataSource.getMyProfile()

        // then
        val errorResponse = response as NetworkResult.Error
        assertEquals(NetworkErrorType.Unexpected(exception), errorResponse.error)
    }

    @Test
    fun `나의 프로필 조회 - HTTP 상태코드 404 응답 시 NotFound 에러로 반환한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(404)
            },
        )

        // when
        val response = dataSource.getMyProfile()

        // then
        val error = (response as NetworkResult.Error).error as NetworkErrorType.Network.HttpError
        assertEquals(404, error.status)
    }

    @Test
    fun `사용자 프로필 조회 - 성공 테스트`() = runTest {
        // given
        val testUserProfileResponseJson = testRule.encodeToJson(TestUserProfileResponse)
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(testUserProfileResponseJson)
            },
        )

        // when
        val response = dataSource.getUserProfile(TestUserId)

        // then
        val successResponse = response as NetworkResult.Success
        assertEquals(TestUserProfileResponse.displayId, successResponse.data.displayId)
        assertEquals(TestUserProfileResponse.name, successResponse.data.name)
    }

    @Test
    fun `사용자 프로필 조회 - 잘못된 응답 바디로 응답 시 알려진 에러를 반환한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(TestInvalidResponse)
            },
        )

        // when
        val response = dataSource.getUserProfile(TestUserId)

        // then
        val errorResponse = response as NetworkResult.Error
        assertEquals(NetworkErrorType.Exception.JsonData, errorResponse.error)
    }

    @Test
    fun `사용자 프로필 조회 - 알 수 없는 예외 발생 시 Unexpected 에러를 반환한다`() = runTest {
        // given
        val mockApi: UserApi = mockk()
        val exception = Exception()
        dataSource = UserNetworkDataSourceImpl(mockApi)
        coEvery { mockApi.getUserProfile(TestUserId.value) } throws exception

        // when
        val response = dataSource.getUserProfile(TestUserId)

        // then
        val errorResponse = response as NetworkResult.Error
        assertEquals(NetworkErrorType.Unexpected(exception), errorResponse.error)
    }

    @Test
    fun `사용자 프로필 조회 - HTTP 상태코드 404 응답 시 NotFound 에러로 반환한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(404)
            },
        )

        // when
        val response = dataSource.getUserProfile(TestUserId)

        // then
        val error = (response as NetworkResult.Error).error as NetworkErrorType.Network.HttpError
        assertEquals(404, error.status)
    }

    @Test
    fun `사용자 수정 - 성공 테스트`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
            },
        )

        // when
        val response = dataSource.updateUser(TestUserPatchRequest)

        // then
        assertTrue(response is NetworkResult.Success)
    }

    @Test
    fun `사용자 수정 - 알 수 없는 예외 발생 시 Unexpected 에러를 반환한다`() = runTest {
        // given
        val mockApi: UserApi = mockk()
        val exception = Exception()
        dataSource = UserNetworkDataSourceImpl(mockApi)
        coEvery { mockApi.updateUser(TestUserPatchRequest) } throws exception

        // when
        val response = dataSource.updateUser(TestUserPatchRequest)

        // then
        val errorResponse = response as NetworkResult.Error
        assertEquals(NetworkErrorType.Unexpected(exception), errorResponse.error)
    }

    @Test
    fun `사용자 수정 - HTTP 상태코드 404 응답 시 NotFound 에러로 반환한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(404)
            },
        )

        // when
        val response = dataSource.updateUser(TestUserPatchRequest)

        // then
        val error = (response as NetworkResult.Error).error as NetworkErrorType.Network.HttpError
        assertEquals(404, error.status)
    }

    @Test
    fun `사용자 소개글 수정 - 성공 테스트`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
            },
        )

        // when
        val response = dataSource.updateIntroduce(TestIntroducePatchRequest)

        // then
        assertTrue(response is NetworkResult.Success)
    }

    @Test
    fun `사용자 소개글 수정 - 알 수 없는 예외 발생 시 Unexpected 에러를 반환한다`() = runTest {
        // given
        val mockApi: UserApi = mockk()
        val exception = Exception()
        dataSource = UserNetworkDataSourceImpl(mockApi)
        coEvery { mockApi.updateIntroduce(TestIntroducePatchRequest) } throws exception

        // when
        val response = dataSource.updateIntroduce(TestIntroducePatchRequest)

        // then
        val errorResponse = response as NetworkResult.Error
        assertEquals(NetworkErrorType.Unexpected(exception), errorResponse.error)
    }

    @Test
    fun `사용자 소개글 수정 - HTTP 상태코드 404 응답 시 NotFound 에러로 반환한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(404)
            },
        )

        // when
        val response = dataSource.updateIntroduce(TestIntroducePatchRequest)

        // then
        val error = (response as NetworkResult.Error).error as NetworkErrorType.Network.HttpError
        assertEquals(404, error.status)
    }

    companion object {
        private val TestMyUserId = UserId(1L)
        private val TestUserId = UserId(2L)
        private val TestDisplayId = DisplayId("did")
        private val TestUserResponse =
            UserResponse(
                id = TestMyUserId.value,
                role = Role.USER,
                provider = SocialLoginProvider.GOOGLE,
                providerId = "pid",
                displayId = TestDisplayId.value,
                name = "name",
                profileImageUrl = "asd",
                introduce = "hello",
                lastLoginAt = 1000L,
                active = true,
            )
        private val TestInvalidResponse =
            """
            {
                "what": "???"
            }
            """.trimIndent()
        private val TestUserPatchRequest = UserPatchRequest(
            name = "name",
            displayId = "id",
            oldProfileImageUrl = null,
            newProfileImageUrl = null,
            introduce = "hello",
        )
        private val TestIntroducePatchRequest = IntroducePatchRequest("hello")
        private val TestMyProfileResponse =
            MyProfileResponse(
                userId = TestMyUserId.value,
                displayId = "did",
                name = "name",
                profileImageUrl = null,
                introduce = "hello",
                lastLoginAt = 1000L,
                active = true,
                friendsCount = 20L,
            )
        private val TestUserProfileResponse =
            UserProfileResponse(
                userId = TestUserId.value,
                displayId = TestDisplayId.value,
                name = "name",
                profileImageUrl = null,
                introduce = "hello",
                lastLoginAt = 1000L,
                active = true,
                friendsCount = 20L,
                friendStatus = FriendStatus.NOTHING,
                isBlocked = false,
            )
    }
}
