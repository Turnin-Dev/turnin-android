package com.turnin.domain.report.usecase

import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.mapError
import com.turnin.core.domain.report.model.ReportReasons
import com.turnin.core.domain.report.repository.ReportRepository
import com.turnin.domain.report.error.ReportErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * 신고 사유 목록 조회
 *
 * @see invoke
 */
class GetReportReasonsUseCase @Inject constructor(
    private val reportRepository: ReportRepository,
) {
    /**
     * 신고 사유 목록을 조회한다.
     */
    operator fun invoke(): Flow<Result<ReportReasons, ReportErrorType>> =
        reportRepository
            .getReportReasons()
            .mapError { commonError ->
                ReportErrorType.CommonError(commonError)
            }
}
