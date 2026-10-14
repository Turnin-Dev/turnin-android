package com.turnin.core.domain.report.repository

import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.report.model.Report
import com.turnin.core.domain.report.model.ReportReasons
import kotlinx.coroutines.flow.Flow

/**
 * 신고 리포지토리
 */
interface ReportRepository {
    /**
     * 신고 사유 목록 조회
     */
    fun getReportReasons(): Flow<Result<ReportReasons, CommonErrorType>>

    /**
     * 신고 생성
     *
     * @param report 신고 모델
     */
    fun createReport(
        report: Report,
    ): Flow<Result<Unit, CommonErrorType>>
}
