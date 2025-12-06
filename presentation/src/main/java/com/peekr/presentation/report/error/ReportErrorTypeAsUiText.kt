package com.peekr.presentation.report.error

import com.peekr.core.presentation.common.error.asUiText
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.domain.report.error.ReportErrorType
import com.peekr.presentation.R

fun ReportErrorType.asUiText(): UiText = when (this) {
    ReportErrorType.NotSelectedReportReason -> UiText.StringResource(R.string.report_error_not_selected_report_reason)
    ReportErrorType.NotSelectedReportedId -> UiText.StringResource(R.string.report_error_not_selected_reporter_id)
    ReportErrorType.UserIdNotFound -> UiText.StringResource(R.string.report_error_user_id_not_found)
    is ReportErrorType.Unexpected -> UiText.StringResource(R.string.report_error_unexpected)
    is ReportErrorType.CommonError -> this.error.asUiText()
}
