package com.turnin.core.domain.report.model

/**
 * 신고 사유 목록
 *
 * @property reasons 신고 사유 목록
 */
data class ReportReasons(
    val reasons: List<ReportReason>,
)
