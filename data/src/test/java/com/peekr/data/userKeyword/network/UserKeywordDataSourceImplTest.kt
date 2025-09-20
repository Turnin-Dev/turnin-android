package com.peekr.data.userKeyword.network

import com.peekr.data.ServerTestRule
import com.peekr.data.common.util.network.NetworkErrorType
import com.peekr.data.common.util.network.NetworkResult
import com.peekr.data.userKeyword.model.request.CreateUserKeywordRequest
import com.peekr.data.userKeyword.model.request.PatchUserKeywordRequest
import com.peekr.data.userKeyword.model.response.UserKeywordResponse
import com.peekr.data.userKeyword.model.response.UserKeywordsResponse
import com.peekr.domain.common.model.KeywordId
import com.peekr.domain.common.model.UserId
import com.peekr.domain.common.model.UserKeywordId
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UserKeywordDataSourceImplTest {
    @get:Rule
    val testRule = ServerTestRule()

    private val userKeywordApi: UserKeywordApi
        get() = testRule.createNetworkApi<UserKeywordApi>(testRule.moshi)

    private lateinit var dataSource: UserKeywordDataSource

    @Before
    fun setUp() {
        dataSource = UserKeywordDataSourceImpl(userKeywordApi)
    }

    @Test
    fun `사용자 키워드 리스트 조회 - 성공 테스트`() = runTest {
        // given
        val expectedResponse = testRule.encodeToJson(TestUserKeywordsResponse)
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(expectedResponse)
            },
        )

        // when
        val response = dataSource.getUserKeywords(TestUserId)

        // then
        assertTrue(response is NetworkResult.Success)
        assertEquals(
            (response as NetworkResult.Success).data,
            TestUserKeywordsResponse,
        )
    }

    @Test
    fun `사용자 키워드 리스트 조회 - 잘못된 응답 바디로 응답 시 알려진 예외를 반환한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(TestInvalidJson)
            },
        )

        // when
        val response = dataSource.getUserKeywords(TestUserId)

        // then
        assertTrue(response is NetworkResult.Error)
        assertEquals(
            NetworkErrorType.Exception.JsonData,
            (response as NetworkResult.Error).error,
        )
    }

    @Test
    fun `사용자 키워드 리스트 조회 - 알 수 없는 예외 발생 시 Unexpected 에러를 반환한다`() = runTest {
        // given
        val mockApi: UserKeywordApi = mockk()
        val exception = Exception()
        dataSource = UserKeywordDataSourceImpl(mockApi)
        coEvery { mockApi.getUserKeywords(TestUserId.value) } throws exception

        // when
        val response = dataSource.getUserKeywords(TestUserId)

        // then
        assertTrue(response is NetworkResult.Error)
        assertEquals(
            NetworkErrorType.Unexpected(exception),
            (response as NetworkResult.Error).error,
        )
    }

    @Test
    fun `사용자 키워드 리스트 조회 - HTTP 상태코드 404 응답 시 NotFound 에러를 반환한다`() = runTest {
        // given
        val expectedResponse = testRule.encodeToJson(TestUserKeywordsResponse)
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(404)
                setBody(expectedResponse)
            },
        )

        // when
        val response = dataSource.getUserKeywords(TestUserId)

        // then
        assertTrue(response is NetworkResult.Error)
        assertEquals(
            NetworkErrorType.Network.NotFound,
            (response as NetworkResult.Error).error,
        )
    }

    @Test
    fun `사용자 키워드 생성 - 성공 테스트`() = runTest {
        // given
        val expectedResponse = testRule.encodeToJson(TestUserKeywordResponse)
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(expectedResponse)
            },
        )

        // when
        val response = dataSource.createUserKeyword(TestCreateUserKeywordRequest)

        // then
        assertTrue(response is NetworkResult.Success)
        assertEquals(
            (response as NetworkResult.Success).data,
            TestUserKeywordResponse,
        )
    }

    @Test
    fun `사용자 키워드 생성 - 잘못된 응답 바디로 응답 시 알려진 예외를 반환한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(TestInvalidJson)
            },
        )

        // when
        val response = dataSource.createUserKeyword(TestCreateUserKeywordRequest)

        // then
        assertTrue(response is NetworkResult.Error)
        assertEquals(
            NetworkErrorType.Exception.JsonData,
            (response as NetworkResult.Error).error,
        )
    }

    @Test
    fun `사용자 키워드 생성 - 알 수 없는 예외 발생 시 Unexpected 에러를 반환한다`() = runTest {
        // given
        val mockApi: UserKeywordApi = mockk()
        val exception = Exception()
        dataSource = UserKeywordDataSourceImpl(mockApi)
        coEvery { mockApi.createUserKeyword(TestCreateUserKeywordRequest) } throws exception

        // when
        val response = dataSource.createUserKeyword(TestCreateUserKeywordRequest)

        // then
        assertTrue(response is NetworkResult.Error)
        assertEquals(
            NetworkErrorType.Unexpected(exception),
            (response as NetworkResult.Error).error,
        )
    }

    @Test
    fun `사용자 키워드 생성 - HTTP 상태코드 404 응답 시 NotFound 에러를 반환한다`() = runTest {
        // given
        val expectedResponse = testRule.encodeToJson(TestUserKeywordsResponse)
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(404)
                setBody(expectedResponse)
            },
        )

        // when
        val response = dataSource.createUserKeyword(TestCreateUserKeywordRequest)

        // then
        assertTrue(response is NetworkResult.Error)
        assertEquals(
            NetworkErrorType.Network.NotFound,
            (response as NetworkResult.Error).error,
        )
    }

    @Test
    fun `사용자 키워드 수정 - 성공 테스트`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(204)
            },
        )

        // when
        val response = dataSource.patchUserKeyword(
            ownerId = TestUserId,
            userKeywordId = TestUserKeywordId,
            patchUserKeywordRequest = TestPatchUserKeywordRequest,
        )

        // then
        assertTrue(response is NetworkResult.Success)
    }

    @Test
    fun `사용자 키워드 수정 - 알 수 없는 예외 발생 시 Unexpected 에러를 반환한다`() = runTest {
        // given
        val mockApi: UserKeywordApi = mockk()
        val exception = Exception()
        dataSource = UserKeywordDataSourceImpl(mockApi)
        coEvery {
            mockApi.patchUserKeyword(
                ownerId = TestUserId.value,
                userKeywordId = TestUserKeywordId.value,
                patchUserKeywordRequest = TestPatchUserKeywordRequest,
            )
        } throws exception

        // when
        val response = dataSource.patchUserKeyword(
            ownerId = TestUserId,
            userKeywordId = TestUserKeywordId,
            patchUserKeywordRequest = TestPatchUserKeywordRequest,
        )

        // then
        assertTrue(response is NetworkResult.Error)
        assertEquals(
            NetworkErrorType.Unexpected(exception),
            (response as NetworkResult.Error).error,
        )
    }

    @Test
    fun `사용자 키워드 수정 - HTTP 상태코드 404 응답 시 NotFound 에러를 반환한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(404)
            },
        )

        // when
        val response = dataSource.patchUserKeyword(
            ownerId = TestUserId,
            userKeywordId = TestUserKeywordId,
            patchUserKeywordRequest = TestPatchUserKeywordRequest,
        )

        // then
        assertTrue(response is NetworkResult.Error)
        assertEquals(
            NetworkErrorType.Network.NotFound,
            (response as NetworkResult.Error).error,
        )
    }

    @Test
    fun `사용자 키워드 삭제 - 성공 테스트`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(204)
            },
        )

        // when
        val response = dataSource.deleteUserKeyword(
            ownerId = TestUserId,
            userKeywordId = TestUserKeywordId,
        )

        // then
        assertTrue(response is NetworkResult.Success)
    }

    @Test
    fun `사용자 키워드 삭제 - 알 수 없는 예외 발생 시 Unexpected 에러를 반환한다`() = runTest {
        // given
        val mockApi: UserKeywordApi = mockk()
        val exception = Exception()
        dataSource = UserKeywordDataSourceImpl(mockApi)
        coEvery {
            mockApi.deleteUserKeyword(
                ownerId = TestUserId.value,
                userKeywordId = TestUserKeywordId.value,
            )
        } throws exception

        // when
        val response = dataSource.deleteUserKeyword(
            ownerId = TestUserId,
            userKeywordId = TestUserKeywordId,
        )

        // then
        assertTrue(response is NetworkResult.Error)
        assertEquals(
            NetworkErrorType.Unexpected(exception),
            (response as NetworkResult.Error).error,
        )
    }

    @Test
    fun `사용자 키워드 삭제 - HTTP 상태코드 404 응답 시 NotFound 에러를 반환한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(404)
            },
        )

        // when
        val response = dataSource.deleteUserKeyword(
            ownerId = TestUserId,
            userKeywordId = TestUserKeywordId,
        )

        // then
        assertTrue(response is NetworkResult.Error)
        assertEquals(
            NetworkErrorType.Network.NotFound,
            (response as NetworkResult.Error).error,
        )
    }

    companion object {
        private val TestUserId = UserId(1L)
        private val TestUserKeywordId = UserKeywordId(1L)
        private val TestKeywordId = KeywordId(1L)
        private val TestInvalidJson =
            """
            {
                "what": "???"
            }
            """.trimIndent()
        private val TestUserKeywordResponse = UserKeywordResponse(
            id = TestUserKeywordId.value,
            keywordId = TestKeywordId.value,
            userId = TestUserId.value,
            offsetX = 0.0,
            offsetY = 0.0,
            description = "sample",
            createdAt = 1000,
            updatedAt = 1000,
        )
        private val TestUserKeywordsResponse = UserKeywordsResponse(
            keywords = listOf(TestUserKeywordResponse),
        )
        private val TestCreateUserKeywordRequest = CreateUserKeywordRequest(
            userId = TestUserId.value,
            keywordId = TestKeywordId.value,
            offsetX = 0.0,
            offsetY = 0.0,
            description = "sample",
        )
        private val TestPatchUserKeywordRequest = PatchUserKeywordRequest(
            offsetX = 0.0,
            offsetY = 0.0,
            description = "sample",
        )
    }
}
