package com.peekr.presentation.profile.view.modal

import androidx.annotation.StringRes
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.peekr.core.presentation.ui.component.modal.PeekrSimpleModal

/**
 * 키워드 삭제 경고 모달
 *
 * @param modifier [Modifier]
 * @param isOpen 모달 활성화 여부
 * @param title 모달 타이틀
 * @param onAcceptClick 경고 모달에서 확인 클릭 시 수행할 작업
 * @param onCancelClick 경고 모달에서 취소 클릭 시 수행할 작업
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafeDeleteModal(
    modifier: Modifier = Modifier,
    isOpen: Boolean,
    @StringRes title: Int,
    onAcceptClick: () -> Unit,
    onCancelClick: () -> Unit,
) {
    PeekrSimpleModal(
        modifier = modifier,
        isOpen = isOpen,
        title = title,
        onAcceptClick = onAcceptClick,
        onCancelClick = onCancelClick,
    )
}
