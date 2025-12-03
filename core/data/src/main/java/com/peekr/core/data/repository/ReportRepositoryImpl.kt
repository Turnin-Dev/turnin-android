package com.peekr.core.data.repository

import com.peekr.core.common.coroutine.IO
import com.peekr.core.data.source.network.datasource.ReportNetworkDataSource
import com.peekr.core.data.source.network.dto.report.request.toDataModel
import com.peekr.core.data.source.network.dto.report.response.toDomainModel
import com.peekr.core.data.source.network.error.toCommonErrorType
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.domain.common.CommonErrorType
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.coroutine.safeResultFlow
import com.peekr.core.domain.report.model.Report
import com.peekr.core.domain.report.model.ReportReasons
import com.peekr.core.domain.report.repository.ReportRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class ReportRepositoryImpl @Inject constructor(
    private val reportNetworkDataSource: ReportNetworkDataSource,
    @IO private val ioDispatcher: CoroutineDispatcher,
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
