package com.peekr.core.data.keyword.network

import com.peekr.core.data.ServerTestRule
import com.peekr.core.data.keyword.model.request.CreateKeywordRequest
import com.peekr.core.data.keyword.model.response.KeywordResponse
import com.peekr.core.data.network.util.NetworkErrorType
import com.peekr.core.data.network.util.NetworkResult
import com.peekr.core.domain.model.KeywordId
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class KeywordDataSourceImplTest {
    @get:Rule
    val testRule = ServerTestRule()

    private val keywordApi: KeywordApi
        get() = testRule.createNetworkApi<KeywordApi>(testRule.moshi)

    private lateinit var dataSource: KeywordDataSource

    @Before
    fun setUp() {
        dataSource = KeywordDataSourceImpl(keywordApi)
    }

    @Test
    fun `키워드 조회 - 성공 테스트`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(TestKeywordResponse)
            },
        )

        // when
        val response = dataSource.getKeyword(TestKeywordId)
        val expectedKeywordResponse =
            testRule.decodeFromJson<KeywordResponse>(TestKeywordResponse)

        // then
        assertTrue(response is NetworkResult.Success)
        assertEquals(
            expectedKeywordResponse,
            (response as NetworkResult.Success).data,
        )
    }

    @Test
    fun `키워드 조회 - 잘못된 응답 바디로 응답 시 알려진 에러를 반환한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(TestInvalidResponse)
            },
        )

        // when
        val response = dataSource.getKeyword(TestKeywordId)

        // then
        assertTrue(response is NetworkResult.Error)
        assertEquals((response as NetworkResult.Error).error, NetworkErrorType.Exception.JsonData)
    }

    @Test
    fun `키워드 조회 - 알 수 없는 예외 발생 시 Unexpected 에러를 반환한다`() = runTest {
        // given
        val mockApi: KeywordApi = mockk()
        val exception = Exception()
        dataSource = KeywordDataSourceImpl(mockApi)
        coEvery { mockApi.getKeyword(any()) } throws exception

        // when
        val response = dataSource.getKeyword(TestKeywordId)

        // then
        assertTrue(response is NetworkResult.Error)
        assertEquals((response as NetworkResult.Error).error, NetworkErrorType.Unexpected(exception))
    }

    @Test
    fun `키워드 조회 - HTTP 상태코드 404 응답 시 NotFound 에러로 반환한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(404)
                setBody(TestKeywordResponse)
            },
        )

        // when
        val response = dataSource.getKeyword(TestKeywordId)

        // then
        assertTrue(response is NetworkResult.Error)
        assertEquals((response as NetworkResult.Error).error, NetworkErrorType.Network.NotFound)
    }

    @Test
    fun `키워드 생성 - 성공 테스트`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(201)
                setBody(TestKeywordResponse)
            },
        )

        // when
        val response = dataSource.createKeyword(TestCreateKeywordRequest)

        // then
        assertTrue(response is NetworkResult.Success)
        assertEquals(
            TestCreateKeywordRequest.keyword,
            (response as NetworkResult.Success).data.keyword,
        )
    }

    @Test
    fun `키워드 생성 - 잘못된 응답 바디로 응답 시 알려진 에러를 반환한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(201)
                setBody(TestInvalidResponse)
            },
        )

        // when
        val response = dataSource.createKeyword(TestCreateKeywordRequest)

        // then
        assertTrue(response is NetworkResult.Error)
        assertEquals((response as NetworkResult.Error).error, NetworkErrorType.Exception.JsonData)
    }

    @Test
    fun `키워드 생성 - 알 수 없는 예외 발생 시 Unexpected 에러를 반환한다`() = runTest {
        // given
        val mockApi: KeywordApi = mockk()
        val exception = Exception()
        dataSource = KeywordDataSourceImpl(mockApi)
        coEvery { mockApi.createKeyword(any()) } throws exception
        // when
        val response = dataSource.createKeyword(TestCreateKeywordRequest)

        // then
        assertTrue(response is NetworkResult.Error)
        assertEquals((response as NetworkResult.Error).error, NetworkErrorType.Unexpected(exception))
    }

    companion object {
        private val TestKeywordId = KeywordId(1L)
        private const val TEST_KEYWORD = "sample"
        private val TestKeywordResponse =
            """
            {
              "id": ${TestKeywordId.value},
              "keyword": "$TEST_KEYWORD",
              "createdBy": 1,
              "createdAt": 1758013900512,
              "updatedAt": 1758013900512
            }
            """.trimIndent()
        private val TestInvalidResponse =
            """
            {
                "what": "???"
            }
            """.trimIndent()
        private val TestCreateKeywordRequest = CreateKeywordRequest(TEST_KEYWORD)
    }
}
