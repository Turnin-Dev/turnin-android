package com.peekr.core.domain.report.model

import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId

/**
 * 신고
 *
 * @property reporterId 신고자 ID
 * @property reportedId 신고 대상 사용자 ID
 * @property reportedUserKeywordId 신고 대상 사용자 키워드 ID
 * @property reasonId 신고 사유 ID
 * @property customReason 기타 신고 사유
 */
data class Report(
    val reporterId: UserId,
    val reportedId: UserId?,
    val reportedUserKeywordId: UserKeywordId?,
    val reasonId: ReportReasonId,
    val customReason: String?,
)
