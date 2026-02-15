package com.peekr.presentation.block.view.modal

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.presentation.ui.component.modal.InputReportBlockReasonModal
import com.peekr.presentation.R

/**
 * 차단 사유 작성 모달
 *
 * @param modifier [Modifier]
 * @param sheetState [SheetState]
 * @param loading 로딩 여부
 * @param onDismissRequest 모달이 사라질 때 수행할 콜백
 * @param onBlock 차단 수행 시 콜백
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputBlockReasonModal(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    loading: Boolean,
    onDismissRequest: () -> Unit,
    onBlock: (String) -> Unit,
) {
    InputReportBlockReasonModal(
        modifier = modifier,
        sheetState = sheetState,
        title = stringResource(R.string.input_block_reason_modal_title),
        placeholder = stringResource(R.string.input_block_reason_modal_tf_placeholder),
        btnTitle = stringResource(R.string.input_block_reason_modal_btn_block),
        loading = loading,
        onDismissRequest = onDismissRequest,
        onFinish = onBlock,
    )
}

// ------------------------------ Previews ------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun InputBlockReasonPreview() {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    PeekrAppTheme {
        InputBlockReasonModal(
            sheetState = sheetState,
            loading = false,
            onDismissRequest = {},
            onBlock = {},
        )
    }
}
