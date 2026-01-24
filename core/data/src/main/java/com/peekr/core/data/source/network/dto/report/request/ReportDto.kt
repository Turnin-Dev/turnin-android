package com.peekr.core.data.source.network.dto.report.request

import com.peekr.core.domain.report.model.Report
import com.squareup.moshi.JsonClass

/**
 * 신고 요청 바디
 *
 * @property reporterId 신고자 ID
 * @property reportedId 신고 대상 사용자 ID
 * @property reportedUserKeywordId 신고 대상 사용자 키워드 ID
 * @property reasonId 신고 사유 ID
 * @property customReason 기타 신고 사유
 */
@JsonClass(generateAdapter = true)
data class ReportRequest(
    val reporterId: Long,
    val reportedId: Long?,
    val reportedUserKeywordId: Long?,
    val reasonId: Long,
    val customReason: String?,
)

fun Report.toDataModel(): ReportRequest =
    ReportRequest(
        reporterId = reporterId.value,
        reportedId = reportedId?.value,
        reportedUserKeywordId = reportedUserKeywordId?.value,
        reasonId = reasonId.value,
        customReason = customReason,
    )
