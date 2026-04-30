package com.turnin.presentation.report.model

import com.turnin.core.domain.report.model.ReportReason
import com.turnin.core.domain.report.model.ReportReasonId
import com.turnin.core.presentation.ui.component.modal.SelectableReason

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
    override val description: String,
) : SelectableReason

fun ReportReason.toUiModel(): UiReportReason =
    UiReportReason(
        id = id,
        code = code,
        description = description,
    )
