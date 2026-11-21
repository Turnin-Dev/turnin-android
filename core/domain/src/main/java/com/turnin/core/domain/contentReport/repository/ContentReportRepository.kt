package com.turnin.core.domain.contentReport.repository

import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.contentReport.model.ContentReport
import kotlinx.coroutines.flow.Flow

/**
 * 콘텐츠 신고 리포지토리
 */
interface ContentReportRepository {
    /**
     * 콘텐츠 신고 생성
     *
     * @param contentReport 콘텐츠 신고 모델
     */
    fun createContentReport(
        contentReport: ContentReport,
    ): Flow<Result<Unit, CommonErrorType>>
}
