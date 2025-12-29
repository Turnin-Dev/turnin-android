package com.peekr.core.data.source.network.datasource

import com.peekr.core.data.ServerTestRule
import com.peekr.core.data.source.network.api.KeywordGraphApi
import com.peekr.core.data.source.network.dto.keywordGraph.response.KeywordNodeResponse
import com.peekr.core.data.source.network.dto.keywordGraph.response.NodeContextCursorPageResponse
import com.peekr.core.data.source.network.dto.keywordGraph.response.NodeContextResponse
import com.peekr.core.data.source.network.dto.keywordGraph.response.UserNodeResponse
import com.peekr.core.data.source.network.error.NetworkErrorType
import com.peekr.core.data.source.network.util.NetworkResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class KeywordGraphNetworkDataSourceImplTest {
    @get:Rule
    val testRule = ServerTestRule()

    private val keywordGraphApi: KeywordGraphApi
        get() = testRule.createNetworkApi<KeywordGraphApi>(testRule.moshi)

    private lateinit var dataSource: KeywordGraphNetworkDataSource

    @Before
    fun setUp() {
        dataSource = KeywordGraphNetworkDataSourceImpl(keywordGraphApi)
    }

    @Test
    fun `노드 컨텍스트 조회 - 성공 테스트`() = runTest {
        // given
        val nodeContextCursorPageResponse = NodeContextCursorPageResponse(
            items = listOf(TestNodeContextResponse),
            nextCursor = null,
        )
        val json = testRule.encodeToJson(nodeContextCursorPageResponse)
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(json)
            },
        )

        // when
        val response = dataSource.getNodeContexts(1L, null, 10)

        // then
        val success = response as NetworkResult.Success
        assertEquals(nodeContextCursorPageResponse, success.data)
    }

    @Test
    fun `노드 컨텍스트 조회 - 잘못된 응답 바디로 응답 시 알려진 에러를 반환한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(TestInvalidResponse)
            },
        )

        // when
        val response = dataSource.getNodeContexts(1L, null, 10)

        // then
        val error = response as NetworkResult.Error
        assertEquals(NetworkErrorType.Exception.JsonData, error.error)
    }

    @Test
    fun `노드 컨텍스트 조회 - 알 수 없는 예외 발생 시 Unexpected 에러를 반환한다`() = runTest {
        // given
        val mockApi: KeywordGraphApi = mockk()
        val exception = Exception()
        dataSource = KeywordGraphNetworkDataSourceImpl(mockApi)
        coEvery { mockApi.getNodeContexts(any(), any(), any()) } throws exception

        // when
        val response = dataSource.getNodeContexts(1L, null, 10)

        // then
        val error = response as NetworkResult.Error
        assertEquals(NetworkErrorType.Unexpected(exception), error.error)
    }

    @Test
    fun `노드 컨텍스트 조회 - HTTP 상태코드 404 응답 시 NotFound 에러로 반환한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(404)
            },
        )

        // when
        val response = dataSource.getNodeContexts(1L, null, 10)

        // then
        val error = response as NetworkResult.Error
        assertEquals(404, error.status)
    }

    companion object {
        private val TestUserNodeResponse = UserNodeResponse(
            userId = 1L,
            userName = "name",
            profileImageUrl = null,
        )
        private val TestKeywordNodeResponse = KeywordNodeResponse(
            keywordId = 1L,
            keywordName = "keyword",
            userKeywordId = 1L,
        )
        private val TestNodeContextResponse = NodeContextResponse(
            userNode = TestUserNodeResponse,
            keywordNodes = listOf(TestKeywordNodeResponse),
        )
        private val TestInvalidResponse =
            """
            {
                "what": "???"
            }
            """.trimIndent()
    }
}
