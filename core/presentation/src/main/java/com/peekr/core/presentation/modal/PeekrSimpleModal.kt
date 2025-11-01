package com.peekr.core.presentation.modal

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.component.button.PeekrButtonStyle
import com.peekr.core.designsystem.component.button.PeekrNegativeButton
import com.peekr.core.designsystem.component.button.PeekrSolidButton
import com.peekr.core.designsystem.component.modal.PeekrModalWrapper
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.presentation.R

/**
 * 기본 모달
 *
 * @param modifier [Modifier]
 * @param isOpen 모달 활성화 여부
 * @param title 모달 타이틀
 * @param acceptLabel 확인 버튼 라벨
 * @param cancelLabel 취소 버튼 라벨
 * @param onAcceptClick 확인 클릭 시 수행할 작업
 * @param onCancelClick 취소 클릭 시 수행할 작업
 * @param onAnimationFinished 모달 사라지는 애니메이션이 끝나고 나서 수행할 작업
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeekrSimpleModal(
    modifier: Modifier = Modifier,
    isOpen: Boolean,
    @StringRes title: Int,
    @StringRes acceptLabel: Int = R.string.simple_modal_btn_accept,
    @StringRes cancelLabel: Int = R.string.simple_modal_btn_cancel,
    onAcceptClick: () -> Unit,
    onCancelClick: () -> Unit,
    onAnimationFinished: (() -> Unit)? = null,
) {
    PeekrModalWrapper(
        isOpen = isOpen,
        animated = true,
        onDismissRequest = onCancelClick,
        onAnimationFinished = { onAnimationFinished?.invoke() },
    ) {
        Column {
            Title(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 40.dp),
                text = stringResource(title),
            )
            Buttons(
                modifier = Modifier.fillMaxWidth(),
                acceptLabel = acceptLabel,
                cancelLabel = cancelLabel,
                onAcceptClick = onAcceptClick,
                onCancelClick = onCancelClick,
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
 * @param acceptLabel 확인 버튼 라벨
 * @param cancelLabel 취소 버튼 라벨
 * @param onAcceptClick `확인` 클릭 시 수행할 작업
 * @param onCancelClick `취소` 클릭 시 수행할 작업
 */
@Composable
private fun Buttons(
    modifier: Modifier = Modifier,
    @StringRes acceptLabel: Int,
    @StringRes cancelLabel: Int,
    onAcceptClick: () -> Unit,
    onCancelClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        PeekrSolidButton(
            modifier = Modifier.weight(1f),
            text = stringResource(acceptLabel),
            style = PeekrButtonStyle.Medium,
            onClick = onAcceptClick,
        )
        PeekrNegativeButton(
            modifier = Modifier.weight(1f),
            text = stringResource(cancelLabel),
            style = PeekrButtonStyle.Medium,
            onClick = onCancelClick,
        )
    }
}
