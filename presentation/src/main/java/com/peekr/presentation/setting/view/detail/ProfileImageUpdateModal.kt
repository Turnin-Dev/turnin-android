package com.peekr.presentation.setting.view.detail

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.peekr.core.designsystem.component.modal.ModalContentToken
import com.peekr.core.designsystem.component.modal.PeekrModalBottomSheet
import com.peekr.core.designsystem.component.modal.PeekrModalBottomSheetContent
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.presentation.R

/**
 * 프로필 사진 업데이트 모달
 *
 * @param modifier [Modifier]
 * @param existsProfileImage 프로필 사진 존재 여부
 * @param sheetState [SheetState]
 * @param onDismissRequest 모달이 사라질 때 수행할 동작
 * @param onCancel 취소 클릭 시 콜백
 * @param onImageChange 프로필 사진 변경 클릭 시 콜백
 * @param onImageChangeToDefault 기본 이미지로 변경 클릭 시 콜백
 * @param onImageAdd 프로필 사진 추가 클릭 시 콜백
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileImageUpdateModal(
    modifier: Modifier = Modifier,
    existsProfileImage: Boolean,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onCancel: () -> Unit,
    onImageChange: () -> Unit,
    onImageChangeToDefault: () -> Unit,
    onImageAdd: () -> Unit,
) {
    PeekrModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
    ) { contentModifier ->
        if (existsProfileImage) {
            PeekrModalBottomSheetContent(
                modifier = contentModifier.fillMaxWidth(),
                onCancel = onCancel,
                ModalContentToken(
                    stringResource(R.string.setting_detail_account_info_modal_change_image),
                    PeekrTheme.colorScheme.textNormal,
                    onImageChange,
                ),
                ModalContentToken(
                    stringResource(R.string.setting_detail_account_info_modal_change_default),
                    PeekrTheme.colorScheme.textNormal,
                    onImageChangeToDefault,
                ),
            )
        } else {
            PeekrModalBottomSheetContent(
                modifier = contentModifier.fillMaxWidth(),
                onCancel = onCancel,
                ModalContentToken(
                    stringResource(R.string.setting_detail_account_info_modal_add_image),
                    PeekrTheme.colorScheme.textNormal,
                    onImageAdd,
                ),
            )
        }
    }
}
