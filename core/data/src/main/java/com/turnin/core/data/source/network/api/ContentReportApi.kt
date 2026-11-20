package com.turnin.core.data.source.network.api

import com.turnin.core.data.source.network.dto.contentReport.request.ContentReportRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ContentReportApi {
    /**
     * 콘텐츠 신고 생성
     *
     * @param contentReportRequest 콘텐츠 신고 요청 바디
     */
    @POST(NetworkApiPath.ContentReport.ROUTE)
    suspend fun createContentReport(
        @Body contentReportRequest: ContentReportRequest,
    ): Response<Unit>
}
