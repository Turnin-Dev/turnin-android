package com.peekr.core.data.repository

import com.peekr.core.data.source.network.datasource.ReportNetworkDataSource
import com.peekr.core.data.source.network.dto.report.response.ReportReasonResponse
import com.peekr.core.data.source.network.dto.report.response.ReportReasonsResponse
import com.peekr.core.data.source.network.dto.report.response.toDomainModel
import com.peekr.core.data.source.network.error.NetworkErrorType
import com.peekr.core.data.source.network.error.toCommonErrorType
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.report.model.Report
import com.peekr.core.domain.report.model.ReportReasonId
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReportRepositoryImplTest {
    private val dataSource: ReportNetworkDataSource = mockk()
    private val dispatcher = UnconfinedTestDispatcher()
    private val repository = ReportRepositoryImpl(dataSource, dispatcher)

    @Test
    fun `신고 사유 목록 조회 - 성공 테스트`() = runTest {
        // given
        coEvery {
            dataSource.getReportReasons()
        } returns NetworkResult.Success(TestReportReasonsResponse)

        // when
        val result = repository.getReportReasons().last()

        // then
        val success = result as Result.Success
        assertEquals(
            TestReportReasonsResponse.toDomainModel(),
            success.data,
        )
    }

    @Test
    fun `신고 사유 목록 조회 - 알려진 에러 방출 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = NetworkErrorType.Unexpected(null)
        coEvery {
            dataSource.getReportReasons()
        } returns NetworkResult.Error(expectedError)

        // when
        val result = repository.getReportReasons().last()

        // then
        val error = result as Result.Error
        assertEquals(
            expectedError.toCommonErrorType(),
            error.error,
        )
    }

    @Test
    fun `신고 사유 목록 조회 - 예외 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedException = Exception("error!")
        coEvery {
            dataSource.getReportReasons()
        } throws expectedException

        // when
        val result = repository.getReportReasons().last()

        // then
        assertTrue(result is Result.Error)
        if (result is Result.Error && result.error is CommonErrorType.Unexpected) {
            assertEquals(
                CommonErrorType.Unexpected(expectedException).cause?.message,
                (result.error as CommonErrorType.Unexpected).cause?.message,
            )
        }
    }

    @Test
    fun `신고 생성 - 성공 테스트`() = runTest {
        // given
        coEvery {
            dataSource.createReport(any())
        } returns NetworkResult.Success(Unit)

        // when
        val result = repository.createReport(TestReport).last()

        // then
        assertTrue(result is Result.Success)
    }

    @Test
    fun `신고 생성 - 알려진 에러 방출 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = NetworkErrorType.Unexpected(null)
        coEvery {
            dataSource.createReport(any())
        } returns NetworkResult.Error(expectedError)

        // when
        val result = repository.createReport(TestReport).last()

        // then
        val error = result as Result.Error
        assertEquals(
            expectedError.toCommonErrorType(),
            error.error,
        )
    }

    @Test
    fun `신고 생성 - 예외 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedException = Exception("error!")
        coEvery {
            dataSource.createReport(any())
        } throws expectedException

        // when
        val result = repository.createReport(TestReport).last()

        // then
        assertTrue(result is Result.Error)
        if (result is Result.Error && result.error is CommonErrorType.Unexpected) {
            assertEquals(
                CommonErrorType.Unexpected(expectedException).cause?.message,
                (result.error as CommonErrorType.Unexpected).cause?.message,
            )
        }
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
        private val TestReport = Report(
            reporterId = UserId(1L),
            reportedId = UserId(2L),
            reportedUserKeywordId = null,
            reasonId = ReportReasonId(1L),
            customReason = "reason",
        )
    }
}
