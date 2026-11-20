package com.turnin.core.domain.contentReport.model

import com.turnin.core.domain.model.ReportReasonId

/**
 * 콘텐츠 신고
 *
 * @property type 콘텐츠 신고 타입
 * @property reasonId 신고 사유 ID
 * @property customReason 기타 신고 사유
 */
data class ContentReport(
    val type: ContentReportType,
    val reasonId: ReportReasonId,
    val customReason: String?,
)
