package com.turnin.presentation.report.view

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.turnin.core.designsystem.theme.PeekrAppTheme
import com.turnin.core.presentation.ui.component.modal.InputReportBlockReasonModal
import com.turnin.presentation.R

/**
 * 신고 사유 작성 모달
 *
 * @param modifier [Modifier]
 * @param sheetState [SheetState]
 * @param loading 로딩 여부
 * @param onDismissRequest 모달이 사라질 때 수행할 콜백
 * @param onReport 신고 수행 시 콜백
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputReportReasonModal(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    loading: Boolean,
    onDismissRequest: () -> Unit,
    onReport: (String) -> Unit,
) {
    InputReportBlockReasonModal(
        modifier = modifier,
        sheetState = sheetState,
        title = stringResource(R.string.input_report_reason_modal_title),
        placeholder = stringResource(R.string.input_report_reason_modal_tf_placeholder),
        btnTitle = stringResource(R.string.input_report_reason_modal_btn_report),
        loading = loading,
        onDismissRequest = onDismissRequest,
        onFinish = onReport,
    )
}

// ------------------------------ Previews ------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun InputReportReasonPreview() {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    PeekrAppTheme {
        InputReportReasonModal(
            sheetState = sheetState,
            loading = false,
            onDismissRequest = {},
            onReport = {},
        )
    }
}
