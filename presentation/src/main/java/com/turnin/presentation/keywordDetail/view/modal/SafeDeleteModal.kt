package com.turnin.presentation.keywordDetail.view.modal

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.turnin.core.designsystem.component.modal.PeekrSimpleModal
import com.turnin.core.designsystem.theme.PeekrAppTheme
import com.turnin.presentation.R

/**
 * 키워드 안전 삭제 모달
 *
 * @param modifier [Modifier]
 * @param isOpen 모달 활성화 여부
 * @param title 모달 타이틀
 * @param onAcceptClick 경고 모달에서 확인 클릭 시 수행할 작업
 * @param onCancelClick 경고 모달에서 취소 클릭 시 수행할 작업
 */
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

@Preview
@Composable
private fun SafeDeleteModalPreview() {
    PeekrAppTheme {
        var isOpen by remember { mutableStateOf(false) }

        Box(Modifier.fillMaxSize()) {
            Button(onClick = { isOpen = true }) {
                Text("Modal Open")
            }

            SafeDeleteModal(
                isOpen = isOpen,
                title = R.string.keyword_detail_safe_delete_modal_title,
                onAcceptClick = {},
                onCancelClick = { isOpen = false },
            )
        }
    }
}
