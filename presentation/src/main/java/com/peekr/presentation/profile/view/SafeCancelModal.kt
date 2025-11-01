package com.peekr.presentation.profile.view

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.presentation.modal.PeekrSimpleModal
import com.peekr.presentation.R

/**
 * 키워드 추가 모달에서 작성된 내용이 있는 상태에서 벗어날 때 표시하는 경고 모달
 *
 * @param modifier [Modifier]
 * @param isOpen 모달 활성화 여부
 * @param title 모달 타이틀
 * @param onAcceptClick 경고 모달에서 확인 클릭 시 수행할 작업
 * @param onCancelClick 경고 모달에서 취소 클릭 시 수행할 작업
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafeCancelModal(
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
private fun CancelWarningModalPreview() {
    PeekrAppTheme {
        var isOpen by remember { mutableStateOf(false) }

        Box(Modifier.fillMaxSize()) {
            Button(onClick = { isOpen = true }) {
                Text("Modal Open")
            }

            SafeCancelModal(
                isOpen = isOpen,
                title = R.string.profile_screen_safe_modal_cancel,
                onAcceptClick = {},
                onCancelClick = { isOpen = false },
            )
        }
    }
}
