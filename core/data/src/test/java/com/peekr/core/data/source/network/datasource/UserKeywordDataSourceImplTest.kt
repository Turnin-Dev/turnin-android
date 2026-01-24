package com.peekr.core.data.source.network.datasource

import com.peekr.core.data.ServerTestRule
import com.peekr.core.data.source.network.api.UserKeywordApi
import com.peekr.core.data.source.network.dto.common.UserInfoResponse
import com.peekr.core.data.source.network.dto.common.UserKeywordDetailResponse
import com.peekr.core.data.source.network.dto.userKeyword.request.CreateUserKeywordRequest
import com.peekr.core.data.source.network.dto.userKeyword.request.PatchUserKeywordRequest
import com.peekr.core.data.source.network.dto.userKeyword.response.UserKeywordResponse
import com.peekr.core.data.source.network.dto.userKeyword.response.UserKeywordsResponse
import com.peekr.core.data.source.network.error.NetworkErrorType
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId
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

    private lateinit var dataSource: UserKeywordNetworkDataSource

    @Before
    fun setUp() {
        dataSource = UserKeywordNetworkDataSourceImpl(userKeywordApi)
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
        dataSource = UserKeywordNetworkDataSourceImpl(mockApi)
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
        val error = (response as NetworkResult.Error).error as NetworkErrorType.Network.HttpError
        assertEquals(404, error.status)
    }

    @Test
    fun `사용자 키워드 수정 - 성공 테스트`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
            },
        )

        // when
        val response = dataSource.patch(TestPatchUserKeywordRequest)

        // then
        assertTrue(response is NetworkResult.Success)
    }

    @Test
    fun `사용자 키워드 수정 - 알 수 없는 예외 발생 시 Unexpected 에러를 반환한다`() = runTest {
        // given
        val mockApi: UserKeywordApi = mockk()
        val exception = Exception()
        dataSource = UserKeywordNetworkDataSourceImpl(mockApi)
        coEvery {
            mockApi.patch(patchUserKeywordRequest = TestPatchUserKeywordRequest)
        } throws exception

        // when
        val response = dataSource.patch(TestPatchUserKeywordRequest)

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
        val response = dataSource.patch(TestPatchUserKeywordRequest)

        // then
        assertTrue(response is NetworkResult.Error)
        val error = (response as NetworkResult.Error).error as NetworkErrorType.Network.HttpError
        assertEquals(404, error.status)
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
        val response = dataSource.deleteUserKeyword(userKeywordId = TestUserKeywordId)

        // then
        assertTrue(response is NetworkResult.Success)
    }

    @Test
    fun `사용자 키워드 삭제 - 알 수 없는 예외 발생 시 Unexpected 에러를 반환한다`() = runTest {
        // given
        val mockApi: UserKeywordApi = mockk()
        val exception = Exception()
        dataSource = UserKeywordNetworkDataSourceImpl(mockApi)
        coEvery {
            mockApi.deleteUserKeyword(userKeywordId = TestUserKeywordId.value)
        } throws exception

        // when
        val response = dataSource.deleteUserKeyword(userKeywordId = TestUserKeywordId)

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
        val response = dataSource.deleteUserKeyword(userKeywordId = TestUserKeywordId)

        // then
        assertTrue(response is NetworkResult.Error)
        val error = (response as NetworkResult.Error).error as NetworkErrorType.Network.HttpError
        assertEquals(404, error.status)
    }

    @Test
    fun `사용자 키워드 상세 정보 조회(사용자 정보 포함) - 성공 테스트`() = runTest {
        // given
        val expectedResponse = testRule.encodeToJson(TestUserKeywordDetailResponse)
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(expectedResponse)
            },
        )

        // when
        val response = dataSource.getDetail(TestUserKeywordId)

        // then
        val success = response as NetworkResult.Success
        assertEquals(TestUserKeywordDetailResponse, success.data)
    }

    companion object {
        private val TestUserId = UserId(1L)
        private val TestUserKeywordId = UserKeywordId(1L)
        private val TestKeywordId = KeywordId(1L)
        private const val TEST_KEYWORD_NAME = "sampleKeyword"
        private val TestInvalidJson =
            """
            {
                "what": "???"
            }
            """.trimIndent()
        private val TestUserKeywordResponse = UserKeywordResponse(
            id = TestUserKeywordId.value,
            keywordId = TestKeywordId.value,
            keyword = TEST_KEYWORD_NAME,
            description = "description",
            createdAt = 1000,
            updatedAt = 1000,
        )
        private val TestUserKeywordsResponse = UserKeywordsResponse(
            keywords = listOf(TestUserKeywordResponse),
        )
        private val TestCreateUserKeywordRequest = CreateUserKeywordRequest(
            userId = TestUserId.value,
            keyword = TEST_KEYWORD_NAME,
            description = "sample",
        )
        private val TestUserKeywordDetailResponse = UserKeywordDetailResponse(
            userKeywordId = TestUserKeywordId.value,
            keywordId = TestKeywordId.value,
            keywordName = "keyword",
            description = "description",
            userInfo = UserInfoResponse(
                userId = TestUserId.value,
                userName = "name",
                profileImageUrl = null,
            ),
            createdAt = 0L,
            updatedAt = 0L,
        )
        private val TestPatchUserKeywordRequest = PatchUserKeywordRequest(
            userKeywordId = 1L,
            keywordName = "newKeyword",
            description = "newDescription",
        )
    }
}
