package com.turnin.presentation.report.view

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.turnin.core.designsystem.theme.TurninAppTheme
import com.turnin.core.domain.report.model.ReportReasonId
import com.turnin.core.presentation.ui.component.modal.SelectReportBlockReasonModal
import com.turnin.core.presentation.ui.util.UiText
import com.turnin.presentation.report.model.UiReportReason

/**
 * 신고 사유 선택 모달
 *
 * @param modifier [Modifier]
 * @param sheetState [SheetState]
 * @param reportReasons 신고 사유 목록
 * @param loading 신고 사유 목록 로딩 여부
 * @param error 에러 메시지
 * @param onDismissRequest 모달이 사라질 때 수행할 콜백
 * @param onCancel 모달 취소 시
 * @param onReportReasonsClick 신고 사유 클릭 시
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectReportReasonModal(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    reportReasons: List<UiReportReason>,
    loading: Boolean,
    error: UiText?,
    onDismissRequest: () -> Unit,
    onCancel: () -> Unit,
    onReportReasonsClick: (UiReportReason) -> Unit,
) {
    SelectReportBlockReasonModal(
        modifier = modifier,
        sheetState = sheetState,
        reasons = reportReasons,
        loading = loading,
        error = error,
        onDismissRequest = onDismissRequest,
        onCancel = onCancel,
        onReasonClick = onReportReasonsClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun SelectReportReasonModalPreview() {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    TurninAppTheme {
        SelectReportReasonModal(
            sheetState = sheetState,
            reportReasons = listOf(
                UiReportReason(
                    id = ReportReasonId(1L),
                    code = "SPAM",
                    description = "스팸 및 사기",
                ),
                UiReportReason(
                    id = ReportReasonId(1L),
                    code = "INAPPROPRIATE",
                    description = "부적절한 콘텐츠",
                ),
                UiReportReason(
                    id = ReportReasonId(1L),
                    code = "ETC",
                    description = "기타",
                ),
            ),
            loading = false,
            error = null,
            onDismissRequest = {},
            onCancel = {},
            onReportReasonsClick = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun SkeletonPreview() {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    TurninAppTheme {
        SelectReportReasonModal(
            sheetState = sheetState,
            reportReasons = emptyList(),
            loading = true,
            error = null,
            onDismissRequest = {},
            onCancel = {},
            onReportReasonsClick = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun ErrorPreview() {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    TurninAppTheme {
        SelectReportReasonModal(
            sheetState = sheetState,
            reportReasons = emptyList(),
            loading = false,
            error = UiText.DynamicString("잠시 오류가 발생했어요. 다시 시도해주세요."),
            onDismissRequest = {},
            onCancel = {},
            onReportReasonsClick = {},
        )
    }
}
