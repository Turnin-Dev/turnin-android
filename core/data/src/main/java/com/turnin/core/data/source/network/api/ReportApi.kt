package com.turnin.core.data.source.network.api

import com.turnin.core.data.source.network.dto.report.request.ReportRequest
import com.turnin.core.data.source.network.dto.report.response.ReportReasonsResponse
import com.turnin.core.data.source.network.retrofit.Cacheable
import com.turnin.core.data.source.network.retrofit.HttpCacheDuration
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/** Report Network API */
interface ReportApi {
    /** 신고 사유 목록 조회 */
    @Cacheable(maxAge = HttpCacheDuration.ONE_WEEK)
    @GET(NetworkApiPath.Report.REASON)
    suspend fun getReportReasons(): Response<ReportReasonsResponse>

    /** 신고 생성 */
    @POST(NetworkApiPath.Report.ROUTE)
    suspend fun createReport(
        @Body report: ReportRequest,
    ): Response<Unit>
}
