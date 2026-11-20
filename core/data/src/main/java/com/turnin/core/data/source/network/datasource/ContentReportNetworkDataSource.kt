package com.turnin.core.data.source.network.datasource

import com.turnin.core.data.source.network.dto.contentReport.request.ContentReportRequest
import com.turnin.core.data.source.network.util.NetworkResult

/** 콘텐츠 신고 네트워크 데이터 소스 */
interface ContentReportNetworkDataSource {
    /**
     * 콘텐츠 신고 생성
     *
     * @param contentReportRequest 콘텐츠 신고 요청 바디
     */
    suspend fun createContentReport(
        contentReportRequest: ContentReportRequest,
    ): NetworkResult<Unit>
}
