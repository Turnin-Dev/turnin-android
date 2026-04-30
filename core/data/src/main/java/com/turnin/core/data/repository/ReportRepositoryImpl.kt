package com.turnin.core.data.repository

import com.turnin.core.common.coroutine.IO
import com.turnin.core.data.source.network.datasource.ReportNetworkDataSource
import com.turnin.core.data.source.network.dto.report.request.toDataModel
import com.turnin.core.data.source.network.dto.report.response.toDomainModel
import com.turnin.core.data.source.network.error.toCommonErrorType
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.coroutine.safeResultFlow
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.report.model.Report
import com.turnin.core.domain.report.model.ReportReasons
import com.turnin.core.domain.report.repository.ReportRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class ReportRepositoryImpl @Inject constructor(
    private val reportNetworkDataSource: ReportNetworkDataSource,
    @param:IO private val ioDispatcher: CoroutineDispatcher,
) : ReportRepository {
    override fun getReportReasons(): Flow<Result<ReportReasons, CommonErrorType>> =
        safeResultFlow<ReportReasons, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            when (val result = reportNetworkDataSource.getReportReasons()) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    val error = result.error.toCommonErrorType()
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun createReport(report: Report): Flow<Result<Unit, CommonErrorType>> =
        safeResultFlow<Unit, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            when (val result = reportNetworkDataSource.createReport(report.toDataModel())) {
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
