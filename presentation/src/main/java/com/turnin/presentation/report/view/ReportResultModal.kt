package com.turnin.presentation.report.view

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.turnin.core.designsystem.theme.TurninAppTheme
import com.turnin.core.presentation.ui.component.modal.ReportBlockResultModal
import com.turnin.core.presentation.ui.util.UiText
import com.turnin.presentation.R

/**
 * 신고 결과 모달
 *
 * @param modifier [Modifier]
 * @param sheetState [SheetState]
 * @param error 에러 메시지
 * @param onDismissRequest 모달이 사라질 때 수행할 콜백
 * @param onFinishClick 완료 클릭 시 콜백
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportResultModal(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    error: UiText?,
    onDismissRequest: () -> Unit,
    onFinishClick: () -> Unit,
) {
    ReportBlockResultModal(
        modifier = modifier,
        sheetState = sheetState,
        error = error,
        errorBtnText = stringResource(R.string.report_result_modal_btn_ok),
        normalBtnText = stringResource(R.string.report_result_modal_btn_finish),
        successTitle = stringResource(R.string.report_result_modal_success_title),
        successContent = stringResource(R.string.report_result_modal_success),
        onDismissRequest = onDismissRequest,
        onFinishClick = onFinishClick,
    )
}

// ------------------------------ Preview ------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun ReportResultModalPreview() {
    val sheetState = rememberModalBottomSheetState()

    TurninAppTheme {
        ReportResultModal(
            sheetState = sheetState,
            error = null,
            onDismissRequest = {},
            onFinishClick = {},
        )
    }
}
