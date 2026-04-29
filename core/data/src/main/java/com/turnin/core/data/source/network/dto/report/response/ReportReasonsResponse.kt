package com.turnin.core.data.source.network.dto.report.response

import com.squareup.moshi.JsonClass
import com.turnin.core.domain.report.model.ReportReasons

/**
 * 신고 사유 목록 응답 바디
 *
 * @property reasons 신고 사유 목록
 */
@JsonClass(generateAdapter = true)
data class ReportReasonsResponse(
    val reasons: List<ReportReasonResponse>,
)

fun ReportReasonsResponse.toDomainModel(): ReportReasons =
    ReportReasons(reasons.map { it.toDomainModel() })
