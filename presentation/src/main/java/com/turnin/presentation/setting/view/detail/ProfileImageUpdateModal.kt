package com.turnin.presentation.setting.view.detail

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.turnin.core.designsystem.component.modal.ModalContentToken
import com.turnin.core.designsystem.component.modal.TurninModalBottomSheet
import com.turnin.core.designsystem.component.modal.TurninModalBottomSheetContent
import com.turnin.core.designsystem.theme.TurninTheme
import com.turnin.presentation.R

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
    TurninModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
    ) { contentModifier ->
        if (existsProfileImage) {
            TurninModalBottomSheetContent(
                modifier = contentModifier.fillMaxWidth(),
                onCancel = onCancel,
                ModalContentToken(
                    stringResource(R.string.setting_detail_account_info_modal_change_image),
                    TurninTheme.colorScheme.textNormal,
                    onImageChange,
                ),
                ModalContentToken(
                    stringResource(R.string.setting_detail_account_info_modal_change_default),
                    TurninTheme.colorScheme.textNormal,
                    onImageChangeToDefault,
                ),
            )
        } else {
            TurninModalBottomSheetContent(
                modifier = contentModifier.fillMaxWidth(),
                onCancel = onCancel,
                ModalContentToken(
                    stringResource(R.string.setting_detail_account_info_modal_add_image),
                    TurninTheme.colorScheme.textNormal,
                    onImageAdd,
                ),
            )
        }
    }
}
