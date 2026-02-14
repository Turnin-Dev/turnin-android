package com.peekr.core.presentation.ui.component.modal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.component.button.PeekrButtonStyle
import com.peekr.core.designsystem.component.button.PeekrSolidButton
import com.peekr.core.designsystem.component.modal.PeekrModalBottomSheet
import com.peekr.core.designsystem.component.textfield.PeekrTextField
import com.peekr.core.designsystem.theme.PeekrTheme

/**
 * 신고/차단 사유 작성 모달
 *
 * @param modifier [Modifier]
 * @param sheetState [SheetState]
 * @param title 모달 타이틀
 * @param placeholder 사유 텍스트필드 자리표시자
 * @param btnTitle 버튼 타이틀
 * @param loading 로딩 여부
 * @param onDismissRequest 모달이 사라질 때 수행할 콜백
 * @param onFinish 사유 작성 완료 시 콜백
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputReportBlockReasonModal(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    title: String,
    placeholder: String,
    btnTitle: String,
    loading: Boolean,
    onDismissRequest: () -> Unit,
    onFinish: (String) -> Unit,
) {
    val (reason, onReasonChanged) = rememberSaveable { mutableStateOf("") }

    PeekrModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        sheetGesturesEnabled = false,
        onDismissRequest = onDismissRequest,
    ) { contentModifier ->
        Content(
            modifier = contentModifier.fillMaxWidth(),
            title = title,
            placeholder = placeholder,
            btnTitle = btnTitle,
            loading = loading,
            text = reason,
            onTextChanged = onReasonChanged,
            onClick = { onFinish(reason) },
        )
    }
}

/**
 * 내부 컨텐츠
 *
 * @param modifier [Modifier]
 * @param title 모달 타이틀
 * @param placeholder 사유 텍스트필드 자리표시자
 * @param btnTitle 버튼 타이틀
 * @param loading 로딩 여부
 * @param text 사유 텍스트
 * @param onTextChanged 사유 텍스트 수정 시
 * @param onClick 사유 작성 완료 시 콜백
 */
@Composable
private fun Content(
    modifier: Modifier = Modifier,
    title: String,
    placeholder: String,
    btnTitle: String,
    loading: Boolean,
    text: String,
    onTextChanged: (String) -> Unit,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .heightIn(min = ContentMinHeight),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                style = PeekrTheme.typography.headline2,
                fontWeight = FontWeight.Medium,
                color = PeekrTheme.colorScheme.textNormal,
                textAlign = TextAlign.Center,
            )
            PeekrTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                text = text,
                onTextChanged = onTextChanged,
                placeholder = placeholder,
            )
        }

        PeekrSolidButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            style = PeekrButtonStyle.Medium,
            text = btnTitle,
            loading = loading,
            onClick = onClick,
        )
    }
}

private val ContentMinHeight = 244.dp
