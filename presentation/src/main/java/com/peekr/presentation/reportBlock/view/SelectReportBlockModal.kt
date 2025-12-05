package com.peekr.presentation.reportBlock.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.peekr.core.designsystem.component.modal.ModalContentToken
import com.peekr.core.designsystem.component.modal.PeekrModalBottomSheet
import com.peekr.core.designsystem.component.modal.PeekrModalBottomSheetContent
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.presentation.R

/**
 * 신고/차단 선택 모달
 *
 * @param modifier [Modifier]
 * @param sheetState [SheetState]
 * @param onDismissRequest 모달이 사라질 때 수행할 콜백
 * @param onCancel 모달 취소 시
 * @param onReport 신고 수행 콜백
 * @param onBlock 차단 수행 콜백
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectReportBlockModal(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onCancel: () -> Unit,
    onReport: () -> Unit,
    onBlock: () -> Unit,
) {
    PeekrModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        sheetGesturesEnabled = false,
        onDismissRequest = onDismissRequest,
    ) { contentModifier ->
        PeekrModalBottomSheetContent(
            modifier = contentModifier.fillMaxWidth(),
            onCancel = onCancel,
            ModalContentToken(
                stringResource(R.string.report_block_modal_report),
                PeekrTheme.colorScheme.textNormal,
                onReport,
            ),
            ModalContentToken(
                stringResource(R.string.report_block_modal_block),
                PeekrTheme.colorScheme.statusNegative,
                onBlock,
            ),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun SelectReportBlockModalPreview() {
    var showBottomSheet by remember { mutableStateOf(true) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    PeekrAppTheme {
        SelectReportBlockModal(
            sheetState = sheetState,
            onDismissRequest = { showBottomSheet = false },
            onCancel = { showBottomSheet = false },
            onReport = {},
            onBlock = {},
        )
    }
}
