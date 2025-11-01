package com.peekr.presentation.profile.view

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NodeOptionModal(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit,
) {
    PeekrModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
    ) { contentModifier ->
        PeekrModalBottomSheetContent(
            modifier = contentModifier.fillMaxWidth(),
            onCancel = onCancel,
            ModalContentToken(
                stringResource(R.string.profile_screen_node_option_modal_btn_edit),
                PeekrTheme.colorScheme.textNormal,
                onEdit,
            ),
            ModalContentToken(
                stringResource(R.string.profile_screen_node_option_modal_btn_delete),
                PeekrTheme.colorScheme.statusNegative,
                onDelete,
            ),
        )
    }
}

// ------------------------------ Preview ------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun NodeOptionModalPreview() {
    var showBottomSheet by remember { mutableStateOf(true) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    PeekrAppTheme {
        NodeOptionModal(
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
