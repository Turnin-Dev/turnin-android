package com.peekr.core.data.source.network.dto.report.response

import com.peekr.core.domain.report.model.ReportReason
import com.peekr.core.domain.report.model.ReportReasonId
import com.squareup.moshi.JsonClass

/**
 * 신고 사유 응답 바디
 *
 * @property id 신고 사유 ID
 * @property code 신고 사유 코드
 * @property description 신고 사유 설명
 */
@JsonClass(generateAdapter = true)
data class ReportReasonResponse(
    val id: Long,
    val code: String,
    val description: String,
)

fun ReportReasonResponse.toDomainModel(): ReportReason =
    ReportReason(
        id = ReportReasonId(id),
        code = code,
        description = description,
    )
