package com.peekr.presentation.setting.view.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.component.modal.PeekrCustomModal
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.peekr.presentation.R

/**
 * 계정 삭제 모달
 *
 * @param modifier [Modifier]
 * @param isOpen 모달 활성화 여부
 * @param isDeletionEnabled 삭제 버튼 활성화 여부
 * @param confirmText 삭제 확인 텍스트
 * @param onConfirmTextChanged 삭제 동의 텍스트 변경 시 콜백
 * @param onAcceptClick 삭제 클릭 시 수행할 작업
 * @param onCancelClick 취소 클릭 시 수행할 작업
 */
@Composable
fun DeleteAccountModal(
    modifier: Modifier = Modifier,
    isOpen: Boolean,
    isDeletionEnabled: Boolean,
    confirmText: String,
    onConfirmTextChanged: (String) -> Unit,
    onAcceptClick: () -> Unit,
    onCancelClick: () -> Unit,
) {
    PeekrCustomModal(
        modifier = modifier,
        isOpen = isOpen,
        acceptLabel = R.string.setting_detail_delete_account_modal_delete,
        cancelLabel = R.string.setting_detail_delete_account_modal_cancel,
        onAcceptClick = onAcceptClick,
        onCancelClick = onCancelClick,
        enabledAcceptButton = isDeletionEnabled,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(top = ModalContentTopPaddingDp),
        ) {
            // 타이틀
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.setting_detail_delete_account_modal_title),
                style = PeekrTheme.typography.headline3,
                fontWeight = FontWeight.Medium,
                color = PeekrTheme.colorScheme.textNormal,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(ModalTitleContentGapDp))

            // 설명
            Text(
                text = buildString {
                    append(stringResource(R.string.setting_detail_delete_account_modal_content_1))
                    append("\n\n")
                    append(stringResource(R.string.setting_detail_delete_account_modal_content_2))
                    append("\n")
                    append(stringResource(R.string.setting_detail_delete_account_modal_content_3))
                },
                style = PeekrTheme.typography.headline5,
                fontWeight = FontWeight.Medium,
                color = PeekrTheme.colorScheme.textAssist,
            )

            Spacer(Modifier.height(ModalGapDp))

            // 삭제 확인 텍스트 필드
            DeleteConfirmTextField(
                modifier = Modifier.fillMaxWidth(),
                text = confirmText,
                onTextChanged = onConfirmTextChanged,
            )

            Spacer(Modifier.height(ModalGapDp))
        }
    }
}

/**
 * 삭제 확인 텍스트 필드
 *
 * @param modifier [Modifier]
 * @param text 텍스트
 * @param onTextChanged 텍스트 변화 시 콜백
 */
@Composable
private fun DeleteConfirmTextField(
    modifier: Modifier = Modifier,
    text: String,
    onTextChanged: (String) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(TextFieldGapDp),
    ) {
        Spacer(Modifier.size(1.dp))

        BaseTextField(
            modifier = Modifier.fillMaxWidth(),
            text = text,
            placeholder = buildAnnotatedString {
                append(stringResource(R.string.setting_detail_delete_account_modal_placeholder_prefix))
                append(" ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(stringResource(R.string.setting_detail_delete_account_modal_placeholder_bold))
                }
                append(" ")
                append(stringResource(R.string.setting_detail_delete_account_modal_placeholder_suffix))
            },
            onTextChanged = onTextChanged,
        )

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 0.6.dp,
            color = PeekrTheme.colorScheme.textNormal,
        )
    }
}

/**
 * 베이스 텍스트필드
 *
 * @param modifier [Modifier]
 * @param text 텍스트
 * @param onTextChanged 텍스트 변화 시 콜백
 */
@Composable
private fun BaseTextField(
    modifier: Modifier = Modifier,
    text: String,
    placeholder: AnnotatedString,
    onTextChanged: (String) -> Unit,
) {
    BasicTextField(
        modifier = modifier,
        value = text,
        onValueChange = onTextChanged,
        textStyle = PeekrTheme.typography.body2.copy(
            color = PeekrTheme.colorScheme.textNormal,
            fontWeight = FontWeight.SemiBold,
        ),
        singleLine = true,
    ) { innerTextField ->
        Box(contentAlignment = Alignment.CenterStart) {
            if (text.isEmpty()) {
                Text(
                    text = placeholder,
                    style = PeekrTheme.typography.body2,
                    fontWeight = FontWeight.Normal,
                    color = PeekrTheme.colorScheme.textPlaceholder,
                )
            }
            innerTextField()
        }
    }
}

private val TextFieldGapDp = 10.dp
private val ModalContentTopPaddingDp = 10.dp
private val ModalTitleContentGapDp = 20.dp
private val ModalGapDp = 40.dp

// ------------------------------ Previews ------------------------------
@PreviewLightDarkWithBackground
@Composable
private fun DeleteAccountModalPreview() {
    val (text, onTextChanged) = remember { mutableStateOf("") }

    PeekrAppTheme {
        DeleteAccountModal(
            isOpen = true,
            isDeletionEnabled = text == "삭제",
            confirmText = text,
            onConfirmTextChanged = onTextChanged,
            onAcceptClick = {},
            onCancelClick = {},
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun DeleteConfirmTextFieldPreview() {
    PeekrAppTheme {
        DeleteConfirmTextField(
            modifier = Modifier.fillMaxWidth(),
            text = "",
            onTextChanged = {},
        )
    }
}
