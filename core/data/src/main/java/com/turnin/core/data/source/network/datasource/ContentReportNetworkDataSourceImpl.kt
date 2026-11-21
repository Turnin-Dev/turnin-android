package com.turnin.core.data.source.network.datasource

import com.turnin.core.data.source.network.api.ContentReportApi
import com.turnin.core.data.source.network.dto.contentReport.request.ContentReportRequest
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.data.source.network.util.networkCallWithoutResponse
import javax.inject.Inject

class ContentReportNetworkDataSourceImpl @Inject constructor(
    private val contentReportApi: ContentReportApi,
) : ContentReportNetworkDataSource {
    override suspend fun createContentReport(
        contentReportRequest: ContentReportRequest,
    ): NetworkResult<Unit> =
        networkCallWithoutResponse { contentReportApi.createContentReport(contentReportRequest) }
}
