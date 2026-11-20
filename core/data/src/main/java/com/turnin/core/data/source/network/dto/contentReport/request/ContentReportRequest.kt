package com.turnin.core.data.source.network.dto.contentReport.request

import com.squareup.moshi.JsonClass
import com.turnin.core.domain.contentReport.model.ContentReport
import com.turnin.core.domain.contentReport.model.ContentReportType

/**
 * 콘텐츠 신고 요청 바디
 *
 * @property contentType 신고 대상 콘텐츠 유형
 * @property contentId 신고 대상 콘텐츠 ID ([contentType]에 따라 참조 대상이 다르다)
 * @property reasonId 신고 사유 ID
 * @property customReason 기타 신고 사유
 */
@JsonClass(generateAdapter = true)
data class ContentReportRequest(
    val contentType: String,
    val contentId: Long,
    val reasonId: Long,
    val customReason: String?,
)

fun ContentReport.toDataModel(): ContentReportRequest =
    ContentReportRequest(
        contentType = type.toContentType(),
        contentId = type.toContentId(),
        reasonId = reasonId.value,
        customReason = customReason,
    )

/** 서버에 전달하는 콘텐츠 유형 값 */
internal object ContentReportContentType {
    const val PING_PONG_QUESTION = "PING_PONG_QUESTION"
    const val PING_PONG_ANSWER = "PING_PONG_ANSWER"
}

private fun ContentReportType.toContentType(): String =
    when (this) {
        is ContentReportType.PingPongQuestion -> ContentReportContentType.PING_PONG_QUESTION
        is ContentReportType.PingPongAnswer -> ContentReportContentType.PING_PONG_ANSWER
    }

private fun ContentReportType.toContentId(): Long =
    when (this) {
        is ContentReportType.PingPongQuestion -> pingPongId.value
        is ContentReportType.PingPongAnswer -> pingPongAnswerId.value
    }
