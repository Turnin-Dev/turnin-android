package com.peekr.presentation.reportBlock.model

import com.peekr.core.domain.report.model.ReportReasonId

/**
 * UI용 신고 사유
 *
 * @property id 신고 사유 ID
 * @property code 신고 사유 코드
 * @property description 신고 사유 설명
 */
data class UiReportReason(
    val id: ReportReasonId,
    val code: String,
    val description: String,
)
