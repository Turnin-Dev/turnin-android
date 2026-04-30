package com.turnin.presentation.report.view

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
import com.turnin.core.designsystem.component.modal.ModalContentToken
import com.turnin.core.designsystem.component.modal.TurninModalBottomSheet
import com.turnin.core.designsystem.component.modal.TurninModalBottomSheetContent
import com.turnin.core.designsystem.theme.TurninAppTheme
import com.turnin.core.designsystem.theme.TurninTheme
import com.turnin.presentation.R

/**
 * 신고/차단 선택 모달
 *
 * @param modifier [Modifier]
 * @param sheetState [SheetState]
 * @param onlyReport 신고만 수행할 지에 대한 여부
 * @param onDismissRequest 모달이 사라질 때 수행할 콜백
 * @param onCancel 모달 취소 시
 * @param selectReport 신고 수행 콜백
 * @param selectBlock 차단 수행 콜백
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectReportBlockModal(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    onlyReport: Boolean,
    onDismissRequest: () -> Unit,
    onCancel: () -> Unit,
    selectReport: () -> Unit,
    selectBlock: () -> Unit,
) {
    val modalContentTokens = if (onlyReport) {
        listOf(
            ModalContentToken(
                stringResource(R.string.report_block_modal_report),
                TurninTheme.colorScheme.statusNegative,
                selectReport,
            ),
        )
    } else {
        listOf(
            ModalContentToken(
                stringResource(R.string.report_block_modal_report),
                TurninTheme.colorScheme.textNormal,
                selectReport,
            ),
            ModalContentToken(
                stringResource(R.string.report_block_modal_block),
                TurninTheme.colorScheme.statusNegative,
                selectBlock,
            ),
        )
    }

    TurninModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        sheetGesturesEnabled = false,
        onDismissRequest = onDismissRequest,
    ) { contentModifier ->
        TurninModalBottomSheetContent(
            modifier = contentModifier.fillMaxWidth(),
            onCancel = onCancel,
            *modalContentTokens.toTypedArray(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "신고/차단 모달")
@Composable
private fun SelectReportBlockModalPreview() {
    var showBottomSheet by remember { mutableStateOf(true) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    TurninAppTheme {
        SelectReportBlockModal(
            sheetState = sheetState,
            onlyReport = false,
            onDismissRequest = { showBottomSheet = false },
            onCancel = { showBottomSheet = false },
            selectReport = {},
            selectBlock = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "신고 모달")
@Composable
private fun SelectReportBlockModalPreview2() {
    var showBottomSheet by remember { mutableStateOf(true) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    TurninAppTheme {
        SelectReportBlockModal(
            sheetState = sheetState,
            onlyReport = true,
            onDismissRequest = { showBottomSheet = false },
            onCancel = { showBottomSheet = false },
            selectReport = {},
            selectBlock = {},
        )
    }
}
