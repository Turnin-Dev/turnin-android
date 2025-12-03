package com.peekr.core.domain.report.model

import com.peekr.core.domain.model.UserId

/**
 * 신고
 *
 * @property reporterId 신고자 ID
 * @property reportedId 피신고자 ID
 * @property reasonId 신고 사유 ID
 * @property customReason 기타 신고 사유
 */
data class Report(
    val reporterId: UserId,
    val reportedId: UserId,
    val reasonId: ReportReasonId,
    val customReason: String?,
)
