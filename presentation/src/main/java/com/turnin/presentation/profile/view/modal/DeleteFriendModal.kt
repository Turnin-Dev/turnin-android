package com.turnin.presentation.profile.view.modal

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.turnin.core.designsystem.component.modal.ModalContentToken
import com.turnin.core.designsystem.component.modal.PeekrModalBottomSheet
import com.turnin.core.designsystem.component.modal.PeekrModalBottomSheetContent
import com.turnin.core.designsystem.theme.PeekrTheme
import com.turnin.presentation.R

/**
 * 친구 삭제 모달
 *
 * @param modifier [Modifier]
 * @param sheetState [SheetState]
 * @param onDismissRequest 모달이 사라질 때 수행할 콜백
 * @param onCancel 취소 클릭 시 콜백
 * @param onDeleteFriend 친구 삭제 시 콜백
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteFriendModal(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onCancel: () -> Unit,
    onDeleteFriend: () -> Unit,
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
                stringResource(R.string.user_profile_delete_friend_modal_content),
                PeekrTheme.colorScheme.statusNegative,
                onDeleteFriend,
            ),
        )
    }
}
