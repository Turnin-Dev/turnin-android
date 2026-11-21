package com.turnin.core.data.repository

import com.turnin.core.data.source.network.datasource.ContentReportNetworkDataSource
import com.turnin.core.data.source.network.dto.contentReport.request.ContentReportRequest
import com.turnin.core.data.source.network.error.NetworkErrorType
import com.turnin.core.data.source.network.error.toCommonErrorType
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.contentReport.model.ContentReport
import com.turnin.core.domain.contentReport.model.ContentReportType
import com.turnin.core.domain.model.PingPongAnswerId
import com.turnin.core.domain.model.PingPongId
import com.turnin.core.domain.model.ReportReasonId
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ContentReportRepositoryImplTest {
    private val dataSource: ContentReportNetworkDataSource = mockk()
    private val dispatcher = UnconfinedTestDispatcher()
    private val repository = ContentReportRepositoryImpl(dataSource, dispatcher)

    @Test
    fun `콘텐츠 신고 생성 - 첫 방출로 Loading을 반환한다`() = runTest {
        // given
        coEvery { dataSource.createContentReport(any()) } returns NetworkResult.Success(Unit)

        // when
        val result = repository.createContentReport(TestPingPongQuestionReport).first()

        // then
        assertTrue(result is Result.Loading)
    }

    @Test
    fun `콘텐츠 신고 생성 - 데이터 소스 성공 시 Success를 반환한다`() = runTest {
        // given
        coEvery { dataSource.createContentReport(any()) } returns NetworkResult.Success(Unit)

        // when
        val result = repository.createContentReport(TestPingPongQuestionReport).last()

        // then
        assertTrue(result is Result.Success)
    }

    @Test
    fun `콘텐츠 신고 생성 - 핑퐁 질문 신고는 PING_PONG_QUESTION 유형과 핑퐁 ID로 요청한다`() = runTest {
        // given
        coEvery { dataSource.createContentReport(any()) } returns NetworkResult.Success(Unit)

        // when
        repository.createContentReport(TestPingPongQuestionReport).last()

        // then
        coVerify(exactly = 1) {
            dataSource.createContentReport(
                ContentReportRequest(
                    contentType = "PING_PONG_QUESTION",
                    contentId = TEST_PING_PONG_ID,
                    reasonId = TEST_REASON_ID,
                    customReason = TEST_CUSTOM_REASON,
                ),
            )
        }
    }

    @Test
    fun `콘텐츠 신고 생성 - 핑퐁 답변 신고는 PING_PONG_ANSWER 유형과 답변 ID로 요청한다`() = runTest {
        // given
        coEvery { dataSource.createContentReport(any()) } returns NetworkResult.Success(Unit)

        // when
        repository.createContentReport(TestPingPongAnswerReport).last()

        // then
        coVerify(exactly = 1) {
            dataSource.createContentReport(
                ContentReportRequest(
                    contentType = "PING_PONG_ANSWER",
                    contentId = TEST_PING_PONG_ANSWER_ID,
                    reasonId = TEST_REASON_ID,
                    customReason = null,
                ),
            )
        }
    }

    @Test
    fun `콘텐츠 신고 생성 - 데이터 소스 에러 시 CommonErrorType으로 변환해 반환한다`() = runTest {
        // given
        val networkError = NetworkErrorType.Network.HttpError(status = 404)
        coEvery { dataSource.createContentReport(any()) } returns NetworkResult.Error(networkError)

        // when
        val result = repository.createContentReport(TestPingPongQuestionReport).last()

        // then
        val error = result as Result.Error
        assertEquals(networkError.toCommonErrorType(), error.error)
    }

    @Test
    fun `콘텐츠 신고 생성 - 예외 발생 시 Unexpected 에러를 반환한다`() = runTest {
        // given
        val exception = Exception("error!")
        coEvery { dataSource.createContentReport(any()) } throws exception

        // when
        val result = repository.createContentReport(TestPingPongQuestionReport).last()

        // then
        val error = (result as Result.Error).error as CommonErrorType.Unexpected
        assertSame(exception, error.cause)
    }

    companion object {
        private const val TEST_PING_PONG_ID = 1L
        private const val TEST_PING_PONG_ANSWER_ID = 2L
        private const val TEST_REASON_ID = 3L
        private const val TEST_CUSTOM_REASON = "reason"

        private val TestPingPongQuestionReport = ContentReport(
            type = ContentReportType.PingPongQuestion(PingPongId(TEST_PING_PONG_ID)),
            reasonId = ReportReasonId(TEST_REASON_ID),
            customReason = TEST_CUSTOM_REASON,
        )
        private val TestPingPongAnswerReport = ContentReport(
            type = ContentReportType.PingPongAnswer(PingPongAnswerId(TEST_PING_PONG_ANSWER_ID)),
            reasonId = ReportReasonId(TEST_REASON_ID),
            customReason = null,
        )
    }
}
