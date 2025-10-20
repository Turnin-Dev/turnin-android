package com.peekr.core.data.user.network

import com.peekr.core.data.ServerTestRule
import com.peekr.core.data.network.util.NetworkErrorType
import com.peekr.core.data.network.util.NetworkResult
import com.peekr.core.data.user.network.request.UserPatchRequest
import com.peekr.core.data.user.network.response.UserProfileResponse
import com.peekr.core.data.user.network.response.UserResponse
import com.peekr.core.domain.user.model.UserId
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UserNetworkDataSourceTest {
    @get:Rule
    val testRule = ServerTestRule()

    private val userApi: UserApi
        get() = testRule.createNetworkApi<UserApi>(testRule.moshi)

    private lateinit var dataSource: UserDataSource

    @Before
    fun setUp() {
        dataSource = UserNetworkDataSource(userApi)
    }

    @Test
    fun `사용자 조회 - 성공 테스트`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(TestUserResponseJson)
            },
        )
        val expectedUserResponse =
            testRule.decodeFromJson<UserResponse>(TestUserResponseJson)

        // when
        val response = dataSource.getUser()

        // then
        Assert.assertTrue(response is NetworkResult.Success)
        Assert.assertEquals(expectedUserResponse.id, (response as NetworkResult.Success).data.id)
        Assert.assertEquals(expectedUserResponse.displayId, response.data.displayId)
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
        dataSource = UserNetworkDataSource(mockApi)
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
        Assert.assertTrue(response is NetworkResult.Error)
        Assert.assertEquals(
            NetworkErrorType.Network.NotFound,
            (response as NetworkResult.Error).error,
        )
    }

    @Test
    fun `사용자 프로필 조회 - 성공 테스트`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(TestUserProfileResponseJson)
            },
        )
        val expectedUserResponse =
            testRule.decodeFromJson<UserProfileResponse>(TestUserProfileResponseJson)

        // when
        val response = dataSource.getUserProfile()

        // then
        Assert.assertTrue(response is NetworkResult.Success)
        Assert.assertEquals(expectedUserResponse.id, (response as NetworkResult.Success).data.id)
        Assert.assertEquals(expectedUserResponse.displayId, response.data.displayId)
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
        val response = dataSource.getUserProfile()

        // then
        Assert.assertTrue(response is NetworkResult.Error)
        Assert.assertEquals(
            NetworkErrorType.Exception.JsonData,
            (response as NetworkResult.Error).error,
        )
    }

    @Test
    fun `사용자 프로필 조회 - 알 수 없는 예외 발생 시 Unexpected 에러를 반환한다`() = runTest {
        // given
        val mockApi: UserApi = mockk()
        val exception = Exception()
        dataSource = UserNetworkDataSource(mockApi)
        coEvery { mockApi.getUserProfile() } throws exception

        // when
        val response = dataSource.getUserProfile()

        // then
        Assert.assertTrue(response is NetworkResult.Error)
        Assert.assertEquals(
            NetworkErrorType.Unexpected(exception),
            (response as NetworkResult.Error).error,
        )
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
        val response = dataSource.getUserProfile()

        // then
        Assert.assertTrue(response is NetworkResult.Error)
        Assert.assertEquals(
            NetworkErrorType.Network.NotFound,
            (response as NetworkResult.Error).error,
        )
    }

    @Test
    fun `사용자 수정 - 성공 테스트`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(TestUserResponseJson)
            },
        )

        // when
        val response = dataSource.updateUserById(TestUserPatchRequest)

        // then
        Assert.assertTrue(response is NetworkResult.Success)
    }

    @Test
    fun `사용자 수정 - 알 수 없는 예외 발생 시 Unexpected 에러를 반환한다`() = runTest {
        // given
        val mockApi: UserApi = mockk()
        val exception = Exception()
        dataSource = UserNetworkDataSource(mockApi)
        coEvery { mockApi.updateUser(TestUserPatchRequest) } throws exception

        // when
        val response = dataSource.updateUserById(TestUserPatchRequest)

        // then
        Assert.assertTrue(response is NetworkResult.Error)
        Assert.assertEquals(
            NetworkErrorType.Unexpected(exception),
            (response as NetworkResult.Error).error,
        )
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
        val response = dataSource.updateUserById(TestUserPatchRequest)

        // then
        Assert.assertTrue(response is NetworkResult.Error)
        Assert.assertEquals(
            NetworkErrorType.Network.NotFound,
            (response as NetworkResult.Error).error,
        )
    }

    companion object {
        private val TestUserId = UserId.Companion(1L)
        private val TestUserResponseJson =
            """
            {
              "id": ${TestUserId.value},
              "role": "USER",
              "provider": "GOOGLE",
              "providerId": "1231231231",
              "displayId": "hong_gd_123",
              "name": "honggd",
              "profileImageUrl": "https://www.example.com/image.jpg",
              "introduce": "hello world!",
              "lastLoginAt": 1697875200000,
              "active": true
            }
            """.trimIndent()
        private val TestInvalidResponse =
            """
            {
                "what": "???"
            }
            """.trimIndent()
        private val TestUserPatchRequest = UserPatchRequest(
            displayId = "id",
            name = "name",
            profileImageUrl = null,
            introduce = "hello",
        )
        private val TestUserProfileResponseJson =
            """
            {
              "id": 1,
              "role": "USER",
              "provider": "GOOGLE",
              "providerId": "1231231231",
              "displayId": "hong_gd_123",
              "name": "honggd",
              "profileImageUrl": "https://www.example.com/image.jpg",
              "introduce": "hello world!",
              "lastLoginAt": 1697875200000,
              "friendsCount": 51,
              "active": true
            }
            """.trimIndent()
    }
}
