package com.peekr.presentation.report.error

import com.peekr.core.presentation.common.error.asUiText
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.core.presentation.ui.util.UiText.StringResource
import com.peekr.domain.report.error.ReportErrorType
import com.peekr.presentation.R

fun ReportErrorType.asUiText(): UiText = when (this) {
    ReportErrorType.NotSelectedReportReason -> StringResource(R.string.report_error_not_selected_report_reason)
    ReportErrorType.NotSelectedReportedId -> StringResource(R.string.report_error_not_selected_reported_id)
    ReportErrorType.UserIdNotFound -> StringResource(R.string.report_error_user_id_not_found)
    ReportErrorType.AlreadyReported -> StringResource(R.string.report_error_already_reported)
    ReportErrorType.MissingReportTarget -> StringResource(R.string.report_error_missing_report_target)
    is ReportErrorType.Unexpected -> StringResource(R.string.report_error_unexpected)
    is ReportErrorType.CommonError -> this.error.asUiText()
}
