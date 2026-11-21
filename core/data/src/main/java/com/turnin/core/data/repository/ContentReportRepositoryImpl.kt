package com.turnin.core.data.repository

import com.turnin.core.common.coroutine.IO
import com.turnin.core.data.source.network.datasource.ContentReportNetworkDataSource
import com.turnin.core.data.source.network.dto.contentReport.request.toDataModel
import com.turnin.core.data.source.network.error.toCommonErrorType
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.coroutine.safeResultFlow
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.contentReport.model.ContentReport
import com.turnin.core.domain.contentReport.repository.ContentReportRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class ContentReportRepositoryImpl @Inject constructor(
    private val contentReportNetworkDataSource: ContentReportNetworkDataSource,
    @param:IO private val ioDispatcher: CoroutineDispatcher,
) : ContentReportRepository {
    override fun createContentReport(
        contentReport: ContentReport,
    ): Flow<Result<Unit, CommonErrorType>> =
        safeResultFlow<Unit, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            when (
                val result =
                    contentReportNetworkDataSource.createContentReport(contentReport.toDataModel())
            ) {
                is NetworkResult.Success -> {
                    emit(Result.Success(Unit))
                }

                is NetworkResult.Error -> {
                    val error = result.error.toCommonErrorType()
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }
}
