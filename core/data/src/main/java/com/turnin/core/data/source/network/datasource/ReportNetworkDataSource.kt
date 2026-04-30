package com.turnin.core.data.source.network.datasource

import com.turnin.core.data.source.network.dto.report.request.ReportRequest
import com.turnin.core.data.source.network.dto.report.response.ReportReasonsResponse
import com.turnin.core.data.source.network.util.NetworkResult

/** Report 네트워크 데이터소스 */
interface ReportNetworkDataSource {
    /**
     * 신고 사유 목록 조회
     */
    suspend fun getReportReasons(): NetworkResult<ReportReasonsResponse>

    /**
     * 신고 생성
     */
    suspend fun createReport(
        report: ReportRequest,
    ): NetworkResult<Unit>
}
