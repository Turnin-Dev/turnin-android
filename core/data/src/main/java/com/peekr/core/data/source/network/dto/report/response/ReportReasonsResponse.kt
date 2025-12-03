package com.peekr.core.data.source.network.dto.report.response

import com.peekr.core.domain.report.model.ReportReasons
import com.squareup.moshi.JsonClass

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
