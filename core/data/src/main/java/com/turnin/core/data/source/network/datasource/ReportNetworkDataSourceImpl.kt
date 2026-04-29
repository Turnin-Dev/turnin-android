package com.turnin.core.data.source.network.datasource

import com.turnin.core.data.source.network.api.ReportApi
import com.turnin.core.data.source.network.dto.report.request.ReportRequest
import com.turnin.core.data.source.network.dto.report.response.ReportReasonsResponse
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.data.source.network.util.networkCall
import com.turnin.core.data.source.network.util.networkCallWithoutResponse
import javax.inject.Inject

class ReportNetworkDataSourceImpl @Inject constructor(
    private val reportApi: ReportApi,
) : ReportNetworkDataSource {
    override suspend fun getReportReasons(): NetworkResult<ReportReasonsResponse> =
        networkCall { reportApi.getReportReasons() }

    override suspend fun createReport(report: ReportRequest): NetworkResult<Unit> =
        networkCallWithoutResponse { reportApi.createReport(report) }
}
