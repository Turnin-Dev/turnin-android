package com.turnin.domain.report.usecase

import com.turnin.core.domain.common.Result
import com.turnin.core.domain.report.model.ReportReason
import com.turnin.core.domain.report.model.ReportReasonId
import com.turnin.core.domain.report.model.ReportReasons
import com.turnin.core.domain.report.repository.ReportRepository
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest

class GetReportReasonsUseCaseTest {
    private val reportRepository: ReportRepository = mock()
    private val usecase = GetReportReasonsUseCase(reportRepository)

    @Test
    fun `신고 사유 목록 조회 성공 테스트`() = runTest {
        // given
        every {
            reportRepository.getReportReasons()
        } returns flowOf(Result.Success(TestReportReasons))

        // when
        val result = usecase().last()

        // then
        val success = result as Result.Success
        assertEquals(TestReportReasons, success.data)
    }

    companion object {
        private val TestReportReasons = ReportReasons(
            reasons = listOf(
                ReportReason(
                    id = ReportReasonId(1L),
                    code = "SAMPLE",
                    description = "샘플",
                ),
            ),
        )
    }
}
