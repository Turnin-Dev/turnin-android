package com.peekr.core.data.source.network.datasource

import com.peekr.core.data.ServerTestRule
import com.peekr.core.data.source.network.api.ReportApi
import com.peekr.core.data.source.network.dto.report.request.ReportRequest
import com.peekr.core.data.source.network.dto.report.response.ReportReasonResponse
import com.peekr.core.data.source.network.dto.report.response.ReportReasonsResponse
import com.peekr.core.data.source.network.error.NetworkErrorType
import com.peekr.core.data.source.network.util.NetworkResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ReportNetworkDataSourceImplTest {
    @get:Rule
    val testRule = ServerTestRule()

    private val reportApi: ReportApi
        get() = testRule.createNetworkApi<ReportApi>(testRule.moshi)

    private lateinit var dataSource: ReportNetworkDataSource

    @Before
    fun setUp() {
        dataSource = ReportNetworkDataSourceImpl(reportApi)
    }

    @Test
    fun `신고 사유 목록 조회 - 성공 테스트`() = runTest {
        // given
        val testResponseJson = testRule.encodeToJson(TestReportReasonsResponse)
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(testResponseJson)
            },
        )

        // when
        val response = dataSource.getReportReasons()

        // then
        val success = response as NetworkResult.Success
        assertEquals(
            TestReportReasonsResponse.reasons.size,
            success.data.reasons.size,
        )
        assertEquals(
            TestReportReasonsResponse.reasons.first().description,
            success.data.reasons.first().description,
        )
    }

    @Test
    fun `신고 사유 목록 조회 - 잘못된 응답 바디로 응답 시 알려진 에러를 반환한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
                setBody(TestInvalidResponse)
            },
        )

        // when
        val response = dataSource.getReportReasons()

        // then
        val error = response as NetworkResult.Error
        assertEquals(NetworkErrorType.Exception.JsonData, error.error)
    }

    @Test
    fun `신고 사유 목록 조회 - 알 수 없는 예외 발생 시 Unexpected 에러를 반환한다`() = runTest {
        // given
        val mockApi: ReportApi = mockk()
        val exception = Exception()
        dataSource = ReportNetworkDataSourceImpl(mockApi)
        coEvery { mockApi.getReportReasons() } throws exception

        // when
        val response = dataSource.getReportReasons()

        // then
        assertTrue(response is NetworkResult.Error)
        assertEquals(
            NetworkErrorType.Unexpected(exception),
            (response as NetworkResult.Error).error,
        )
    }

    @Test
    fun `신고 사유 목록 조회 - HTTP 상태코드 404 응답 시 NotFound 에러로 반환한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(404)
            },
        )

        // when
        val response = dataSource.getReportReasons()

        // then
        assertTrue(response is NetworkResult.Error)
        val error = (response as NetworkResult.Error).error as NetworkErrorType.Network.HttpError
        assertEquals(404, error.status)
    }

    @Test
    fun `신고 생성 - 성공 테스트`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
            },
        )

        // when
        val response = dataSource.createReport(TestReportRequest)

        // then
        assertTrue(response is NetworkResult.Success)
    }

    @Test
    fun `신고 생성 - 알 수 없는 예외 발생 시 Unexpected 에러를 반환한다`() = runTest {
        // given
        val mockApi: ReportApi = mockk()
        val exception = Exception()
        dataSource = ReportNetworkDataSourceImpl(mockApi)
        coEvery { mockApi.createReport(any()) } throws exception

        // when
        val response = dataSource.createReport(TestReportRequest)

        // then
        assertTrue(response is NetworkResult.Error)
        assertEquals(
            NetworkErrorType.Unexpected(exception),
            (response as NetworkResult.Error).error,
        )
    }

    @Test
    fun `신고 생성 - HTTP 상태코드 404 응답 시 NotFound 에러로 반환한다`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(404)
            },
        )

        // when
        val response = dataSource.createReport(TestReportRequest)

        // then
        assertTrue(response is NetworkResult.Error)
        val error = (response as NetworkResult.Error).error as NetworkErrorType.Network.HttpError
        assertEquals(404, error.status)
    }

    companion object {
        private val TestReportReasonResponse = ReportReasonResponse(
            id = 1L,
            code = "SPAM",
            description = "스팸 및 사기",
        )
        private val TestReportReasonsResponse = ReportReasonsResponse(
            reasons = listOf(TestReportReasonResponse),
        )
        private val TestInvalidResponse =
            """
            {
                "what": "???"
            }
            """.trimIndent()
        private val TestReportRequest = ReportRequest(
            reporterId = 1L,
            reportedId = 2L,
            reasonId = 1L,
            customReason = "reason",
        )
    }
}
