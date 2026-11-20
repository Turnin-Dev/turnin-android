package com.turnin.core.data.source.network.dto.contentReport.request

/**
 * 콘텐츠 신고 요청 바디
 *
 * @property contentType 신고 대상 콘텐츠 유형
 * @property contentId 신고 대상 콘텐츠 ID ([contentType]에 따라 참조 대상이 다르다)
 * @property reasonId 신고 사유 ID
 * @property customReason 기타 신고 사유
 */
data class ContentReportRequest(
    val contentType: String,
    val contentId: Long,
    val reasonId: Long,
    val customReason: String?,
)
