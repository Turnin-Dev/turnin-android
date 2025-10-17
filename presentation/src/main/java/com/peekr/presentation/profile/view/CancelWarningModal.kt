package com.peekr.presentation.profile.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.component.button.PeekrButtonStyle
import com.peekr.core.designsystem.component.button.PeekrNegativeButton
import com.peekr.core.designsystem.component.button.PeekrSolidButton
import com.peekr.core.designsystem.component.modal.PeekrModalWrapper
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.presentation.R

/**
 * 키워드 추가 모달에서 작성된 내용이 있는 상태에서 벗어날 때 표시하는 경고 모달
 *
 * @param modifier [Modifier]
 * @param isOpen 모달 활성화 여부
 * @param onAnimationFinished 모달 사라지는 애니메이션이 끝나고 나서 수행할 작업
 * @param onDeleteClick 경고 모달에서 삭제 클릭 시 수행할 작업
 * @param onCancel 경고 모달에서 취소 클릭 시 수행할 작업
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CancelWarningModal(
    modifier: Modifier = Modifier,
    isOpen: Boolean,
    onAnimationFinished: (() -> Unit)? = null,
    onDeleteClick: () -> Unit,
    onCancel: () -> Unit,
) {
    PeekrModalWrapper(
        isOpen = isOpen,
        animated = true,
        onDismissRequest = onCancel,
        onAnimationFinished = { onAnimationFinished?.invoke() },
    ) {
        Column {
            Title(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 40.dp),
                text = stringResource(R.string.profile_screen_delete_keyword_modal_title),
            )
            Buttons(
                modifier = Modifier.fillMaxWidth(),
                onDeleteClick = onDeleteClick,
                onCancelClick = onCancel,
            )
        }
    }
}

/**
 * 모달 타이틀
 *
 * @param modifier [Modifier]
 * @param text 타이틀
 */
@Composable
private fun Title(
    modifier: Modifier = Modifier,
    text: String,
) {
    Text(
        modifier = modifier,
        text = text,
        style = PeekrTheme.typography.headline2,
        fontWeight = FontWeight.Medium,
        color = PeekrTheme.colorScheme.textNormal,
        textAlign = TextAlign.Center,
    )
}

/**
 * 하단 버튼 영역
 *
 * @param modifier [Modifier]
 * @param onDeleteClick `삭제` 클릭 시 수행할 작업
 * @param onCancelClick `취소` 클릭 시 수행할 작업
 */
@Composable
private fun Buttons(
    modifier: Modifier = Modifier,
    onDeleteClick: () -> Unit,
    onCancelClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        PeekrSolidButton(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.profile_screen_delete_keyword_modal_btn_delete),
            style = PeekrButtonStyle.Medium,
            onClick = onDeleteClick,
        )
        PeekrNegativeButton(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.profile_screen_delete_keyword_modal_btn_cancel),
            style = PeekrButtonStyle.Medium,
            onClick = onCancelClick,
        )
    }
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

            CancelWarningModal(
                isOpen = isOpen,
                onAnimationFinished = {},
                onDeleteClick = {},
                onCancel = { isOpen = false },
            )
        }
    }
}
