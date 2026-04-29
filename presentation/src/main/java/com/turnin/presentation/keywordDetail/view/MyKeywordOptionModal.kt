package com.turnin.presentation.keywordDetail.view

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
 * 나의 키워드 옵션 모달
 *
 * @param modifier [Modifier]
 * @param sheetState [SheetState]
 * @param onDismissRequest 모달이 사라질 때 수행할 동작
 * @param onEdit 수정 클릭 시 콜백
 * @param onDelete 삭제 클릭 시 콜백
 * @param onCancel 취소 클릭 시 콜백
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyKeywordOptionModal(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit,
) {
    TurninModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
    ) { contentModifier ->
        TurninModalBottomSheetContent(
            modifier = contentModifier.fillMaxWidth(),
            onCancel = onCancel,
            ModalContentToken(
                stringResource(R.string.keyword_detail_option_modal_edit),
                TurninTheme.colorScheme.textNormal,
                onEdit,
            ),
            ModalContentToken(
                stringResource(R.string.keyword_detail_option_modal_delete),
                TurninTheme.colorScheme.statusNegative,
                onDelete,
            ),
        )
    }
}

// ------------------------------ Preview ------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun MyKeywordOptionModalPreview() {
    var showBottomSheet by remember { mutableStateOf(true) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    TurninAppTheme {
        MyKeywordOptionModal(
            sheetState = sheetState,
            onDismissRequest = {
                if (!sheetState.isVisible) {
                    showBottomSheet = false
                }
            },
            onEdit = {},
            onDelete = {},
            onCancel = {},
        )
    }
}
