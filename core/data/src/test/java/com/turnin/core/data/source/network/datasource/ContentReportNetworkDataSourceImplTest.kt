package com.turnin.core.data.source.network.datasource

import com.turnin.core.data.ServerTestRule
import com.turnin.core.data.source.network.api.ContentReportApi
import com.turnin.core.data.source.network.dto.contentReport.request.ContentReportRequest
import com.turnin.core.data.source.network.error.NetworkErrorType
import com.turnin.core.data.source.network.util.NetworkResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ContentReportNetworkDataSourceImplTest {
    @get:Rule
    val testRule = ServerTestRule()

    private val contentReportApi: ContentReportApi
        get() = testRule.createNetworkApi<ContentReportApi>(testRule.moshi)

    private lateinit var dataSource: ContentReportNetworkDataSource

    @Before
    fun setUp() {
        dataSource = ContentReportNetworkDataSourceImpl(contentReportApi)
    }

    @Test
    fun `콘텐츠 신고 생성 - 성공 응답 시 Success를 반환한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
            },
        )

        // when
        val response = dataSource.createContentReport(TestContentReportRequest)

        // then
        assertTrue(response is NetworkResult.Success)
    }

    @Test
    fun `콘텐츠 신고 생성 - POST 메서드로 콘텐츠 신고 경로에 요청한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
            },
        )

        // when
        dataSource.createContentReport(TestContentReportRequest)

        // then
        val request = testRule.server.takeRequest()
        assertEquals("POST", request.method)
        assertEquals("/api/v1/content-report", request.path)
    }

    @Test
    fun `콘텐츠 신고 생성 - 요청 바디에 신고 정보를 담아 보낸다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
            },
        )

        // when
        dataSource.createContentReport(TestContentReportRequest)

        // then
        val requestBody = testRule.server.takeRequest().body.readUtf8()
        assertEquals(
            TestContentReportRequest,
            testRule.decodeFromJson<ContentReportRequest>(requestBody),
        )
    }

    @Test
    fun `콘텐츠 신고 생성 - HTTP 상태코드 404 응답 시 HttpError를 반환한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(404)
            },
        )

        // when
        val response = dataSource.createContentReport(TestContentReportRequest)

        // then
        assertTrue(response is NetworkResult.Error)
        val error = (response as NetworkResult.Error).error as NetworkErrorType.Network.HttpError
        assertEquals(404, error.status)
    }

    @Test
    fun `콘텐츠 신고 생성 - 알 수 없는 예외 발생 시 Unexpected 에러를 반환한다`() = runTest {
        // given
        val mockApi: ContentReportApi = mockk()
        val exception = Exception()
        dataSource = ContentReportNetworkDataSourceImpl(mockApi)
        coEvery { mockApi.createContentReport(any()) } throws exception

        // when
        val response = dataSource.createContentReport(TestContentReportRequest)

        // then
        assertTrue(response is NetworkResult.Error)
        assertEquals(
            NetworkErrorType.Unexpected(exception),
            (response as NetworkResult.Error).error,
        )
    }

    companion object {
        private val TestContentReportRequest = ContentReportRequest(
            contentType = "PING_PONG",
            contentId = 1L,
            reasonId = 2L,
            customReason = "reason",
        )
    }
}
