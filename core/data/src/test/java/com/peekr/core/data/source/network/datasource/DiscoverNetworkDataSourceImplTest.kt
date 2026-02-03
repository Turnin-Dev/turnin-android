package com.peekr.core.data.source.network.datasource

import com.peekr.core.data.ServerTestRule
import com.peekr.core.data.source.network.api.DiscoverApi
import com.peekr.core.data.source.network.dto.discover.response.DiscoverContextCursorPageResponse
import com.peekr.core.data.source.network.dto.discover.response.DiscoverContextResponse
import com.peekr.core.data.source.network.dto.discover.response.DiscoverKeywordResponse
import com.peekr.core.data.source.network.dto.discover.response.DiscoverUserResponse
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

class DiscoverNetworkDataSourceImplTest {
    @get:Rule
    val testRule = ServerTestRule()

    private val discoverApi: DiscoverApi
        get() = testRule.createNetworkApi<DiscoverApi>(testRule.moshi)

    private lateinit var dataSource: DiscoverNetworkDataSource

    @Before
    fun setUp() {
        dataSource = DiscoverNetworkDataSourceImpl(discoverApi)
    }

    @Test
    fun `탐색 컨텍스트 조회 - 성공 테스트`() = runTest {
        // given
        val cursorPageResponse = DiscoverContextCursorPageResponse(
            items = listOf(TestDiscoverContextResponse),
            nextCursor = null,
        )
        val json = testRule.encodeToJson(cursorPageResponse)
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(json)
            },
        )

        // when
        val response = dataSource.getDiscoverContexts(1L, null, 10)

        // then
        val success = response as NetworkResult.Success
        assertEquals(cursorPageResponse, success.data)
    }

    @Test
    fun `탐색 컨텍스트 조회 - 잘못된 응답 바디로 응답 시 알려진 에러를 반환한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(TestInvalidResponse)
            },
        )

        // when
        val response = dataSource.getDiscoverContexts(1L, null, 10)

        // then
        val error = response as NetworkResult.Error
        assertEquals(NetworkErrorType.Exception.JsonData, error.error)
    }

    @Test
    fun `탐색 컨텍스트 조회 - 알 수 없는 예외 발생 시 Unexpected 에러를 반환한다`() = runTest {
        // given
        val mockApi: DiscoverApi = mockk()
        val exception = Exception()
        dataSource = DiscoverNetworkDataSourceImpl(mockApi)
        coEvery { mockApi.getDiscoverContexts(any(), any(), any()) } throws exception

        // when
        val response = dataSource.getDiscoverContexts(1L, null, 10)

        // then
        val error = response as NetworkResult.Error
        assertEquals(NetworkErrorType.Unexpected(exception), error.error)
    }

    @Test
    fun `탐색 컨텍스트 조회 - HTTP 상태코드 404 응답 시 NotFound 에러로 반환한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(404)
            },
        )

        // when
        val response = dataSource.getDiscoverContexts(1L, null, 10)

        // then
        val error = response as NetworkResult.Error
        assertEquals(404, error.status)
    }

    companion object {
        private val TestDiscoverUserResponse = DiscoverUserResponse(
            userId = 1L,
            userName = "name",
            displayId = "did",
            profileImageUrl = null,
        )
        private val TestDiscoverKeywordResponse = DiscoverKeywordResponse(
            keywordId = 1L,
            keywordName = "keyword",
            userKeywordId = 1L,
        )
        private val TestDiscoverContextResponse = DiscoverContextResponse(
            user = TestDiscoverUserResponse,
            keywords = listOf(TestDiscoverKeywordResponse),
        )
        private val TestInvalidResponse =
            """
            {
                "what": "???"
            }
            """.trimIndent()
    }
}
