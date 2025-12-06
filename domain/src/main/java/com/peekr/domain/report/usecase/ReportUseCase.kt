package com.peekr.domain.report.usecase

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.report.model.Report
import com.peekr.core.domain.report.model.ReportReasonId
import com.peekr.core.domain.report.repository.ReportRepository
import com.peekr.core.domain.user.repository.UserRepository
import com.peekr.domain.report.error.ReportErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

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
     * @param reasonId 신고 사유 ID
     * @param customReason 기타 신고 사유
     *
     * @return 신고 성공 시 `true`를 반환하고 중복 신고 시에는 `false`를 반환한다. 그 외에 에러는 [Result.Error]로 반환된다.
     */
    operator fun invoke(
        reportedId: Long,
        reasonId: ReportReasonId,
        customReason: String?,
    ): Flow<Result<Boolean, ReportErrorType>> = flow {
        userRepository.getUserId()?.let { reporterId ->
            val reportedId = UserId(reportedId)
            val report = Report(
                reporterId = reporterId,
                reportedId = reportedId,
                reasonId = reasonId,
                customReason = customReason,
            )

            emitAll(
                reportRepository
                    .createReport(report)
                    .map { result ->
                        when (result) {
                            Result.Loading -> Result.Loading
                            is Result.Success -> Result.Success(true)
                            is Result.Error -> {
                                // 에러 중 Conflict 에러는 중복 신고 시 발생하는 에러이므로 false로 반환
                                if (result.error is CommonErrorType.Network.Conflict) {
                                    Result.Success(false)
                                } else {
                                    Result.Error(ReportErrorType.CommonError(result.error))
                                }
                            }
                        }
                    },
            )
        } ?: emit(Result.Error(ReportErrorType.UserIdNotFound))
    }
}
