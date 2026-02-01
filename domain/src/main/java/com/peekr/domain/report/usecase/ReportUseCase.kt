package com.peekr.domain.report.usecase

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.common.error.mapError
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.report.model.Report
import com.peekr.core.domain.report.model.ReportReasonId
import com.peekr.core.domain.report.repository.ReportRepository
import com.peekr.core.domain.user.repository.UserRepository
import com.peekr.domain.report.error.ReportErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/**
 * 사용자 신고
 *
 * @see invoke
 */
class ReportUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val reportRepository: ReportRepository,
) {
    /**
     * 사용자를 신고한다.
     *
     * @param reportedId 신고할 사용자 ID
     * @param reportedUserKeywordId 신고할 사용자 키워드 ID
     * @param reasonId 신고 사유 ID
     * @param customReason 기타 신고 사유
     */
    operator fun invoke(
        reportedId: Long?,
        reportedUserKeywordId: Long?,
        reasonId: ReportReasonId,
        customReason: String?,
    ): Flow<Result<Unit, ReportErrorType>> = flow {
        // 1) 신고 대상 체크
        if (reportedId == null && reportedUserKeywordId == null) {
            emit(Result.Error(ReportErrorType.MissingReportTarget))
            return@flow
        }

        // 2) 데이터 준비
        val reportedIdVO = reportedId?.let { UserId(it) }
        val reportedUserKeywordIdVO = reportedUserKeywordId?.let { UserKeywordId(it) }
        val reporterIdVO = userRepository.getMyUserId()

        if (reporterIdVO == null) {
            emit(Result.Error(ReportErrorType.UserIdNotFound))
            return@flow
        }

        // 3) 신고 모델 생성
        val report = Report(
            reporterId = reporterIdVO,
            reportedId = reportedIdVO,
            reportedUserKeywordId = reportedUserKeywordIdVO,
            reasonId = reasonId,
            customReason = customReason,
        )

        // 4) 신고 수행
        emitAll(
            reportRepository
                .createReport(report)
                .mapError { commonError ->
                    when (commonError) {
                        is CommonErrorType.Network.BadRequest -> ReportErrorType.MissingReportTarget
                        is CommonErrorType.Network.Conflict -> ReportErrorType.AlreadyReported
                        else -> ReportErrorType.CommonError(commonError)
                    }
                },
        )
    }
}
