package com.peekr.presentation.report.view

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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.component.button.PeekrButtonStyle
import com.peekr.core.designsystem.component.button.PeekrSolidButton
import com.peekr.core.designsystem.component.modal.PeekrModalBottomSheet
import com.peekr.core.designsystem.component.textfield.PeekrTextField
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.peekr.presentation.R

/**
 * 신고 사유 작성 모달
 *
 * @param modifier [Modifier]
 * @param sheetState [SheetState]
 * @param onDismissRequest 모달이 사라질 때 수행할 콜백
 * @param onCancel 모달 취소 시
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputReportReasonModal(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    loading: Boolean,
    onDismissRequest: () -> Unit,
    onCancel: () -> Unit,
    onReport: (String?) -> Unit,
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
            loading = loading,
            text = reason,
            onTextChanged = onReasonChanged,
            onReport = { onReport(reason.ifEmpty { null }) },
        )
    }
}

/**
 * 내부 컨텐츠
 *
 * @param modifier [Modifier]
 * @param loading 신고 후 로딩 여부
 * @param text 신고 사유 텍스트
 * @param onTextChanged 신고 사유 텍스트 수정 시
 * @param onReport 신고 수행 시 콜백
 */
@Composable
private fun Content(
    modifier: Modifier = Modifier,
    loading: Boolean,
    text: String,
    onTextChanged: (String) -> Unit,
    onReport: () -> Unit,
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
                text = stringResource(R.string.input_report_reason_modal_title),
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
                placeholder = stringResource(R.string.input_report_reason_modal_tf_placeholder),
            )
        }

        PeekrSolidButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            style = PeekrButtonStyle.Medium,
            text = stringResource(R.string.input_report_reason_modal_btn_report),
            loading = loading,
            onClick = onReport,
        )
    }
}

private val ContentMinHeight = 244.dp

// ------------------------------ Previews ------------------------------
@PreviewLightDarkWithBackground
@Composable
private fun ContentPreview() {
    val (text, onTextChanged) = remember { mutableStateOf("") }

    PeekrAppTheme {
        Content(
            modifier = Modifier.fillMaxWidth(),
            text = text,
            onTextChanged = onTextChanged,
            loading = false,
            onReport = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun InputReportReasonPreview() {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    PeekrAppTheme {
        InputReportReasonModal(
            sheetState = sheetState,
            loading = false,
            onDismissRequest = {},
            onCancel = {},
            onReport = {},
        )
    }
}
