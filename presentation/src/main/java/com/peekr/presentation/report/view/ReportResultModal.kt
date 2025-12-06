package com.peekr.presentation.report.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.component.button.PeekrButtonStyle
import com.peekr.core.designsystem.component.button.PeekrSolidButton
import com.peekr.core.designsystem.component.modal.PeekrModalBottomSheet
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.peekr.presentation.R

/**
 * 신고 사유 작성 모달
 *
 * @param modifier [Modifier]
 * @param sheetState [SheetState]
 * @param isSuccess 신고 성공 여부
 * @param onDismissRequest 모달이 사라질 때 수행할 콜백
 * @param onCancel 모달 취소 시
 * @param onFinishClick 완료 클릭 시 콜백
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportResultModal(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    isSuccess: Boolean,
    onDismissRequest: () -> Unit,
    onCancel: () -> Unit,
    onFinishClick: () -> Unit,
) {
    PeekrModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        sheetGesturesEnabled = false,
        onDismissRequest = onDismissRequest,
    ) { contentModifier ->
        Content(
            modifier = contentModifier
                .fillMaxWidth()
                .height(ModalContentMinHeight),
            isSuccess = isSuccess,
            onFinishClick = onFinishClick,
        )
    }
}

/**
 * 내부 컨텐츠
 *
 * @param modifier [Modifier]
 * @param isSuccess 신고 성공 여부
 * @param onFinishClick 완료 클릭 시 콜백
 */
@Composable
private fun Content(
    modifier: Modifier = Modifier,
    isSuccess: Boolean,
    onFinishClick: () -> Unit,
) {
    val buttonText = if (isSuccess) {
        stringResource(R.string.report_result_modal_btn_finish)
    } else {
        stringResource(R.string.report_result_modal_btn_ok)
    }

    val alignment = if (isSuccess) {
        Alignment.TopCenter
    } else {
        Alignment.Center
    }

    Column(modifier) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = alignment,
        ) {
            if (!isSuccess) {
                // 중복 신고 시
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.report_result_modal_duplicate_report),
                    style = PeekrTheme.typography.body2,
                    fontWeight = FontWeight.Normal,
                    color = PeekrTheme.colorScheme.textNormal,
                    textAlign = TextAlign.Center,
                )
            } else {
                // 신고 성공 시
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(R.string.report_result_modal_success_title),
                        style = PeekrTheme.typography.headline2,
                        fontWeight = FontWeight.Medium,
                        color = PeekrTheme.colorScheme.textNormal,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = stringResource(R.string.report_result_modal_success),
                        style = PeekrTheme.typography.body2,
                        fontWeight = FontWeight.Normal,
                        color = PeekrTheme.colorScheme.textNormal,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        PeekrSolidButton(
            modifier = Modifier.fillMaxWidth(),
            text = buttonText,
            style = PeekrButtonStyle.Medium,
            onClick = onFinishClick,
        )
    }
}

private val ModalContentMinHeight = 280.dp

// ------------------------------ Preview ------------------------------
@PreviewLightDarkWithBackground
@Composable
private fun ContentIsSuccessPreview() {
    PeekrAppTheme {
        Content(
            modifier = Modifier.height(400.dp),
            isSuccess = true,
            onFinishClick = {},
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun ContentIsNotSuccessPreview() {
    PeekrAppTheme {
        Content(
            modifier = Modifier.height(400.dp),
            isSuccess = false,
            onFinishClick = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun ReportResultModalPreview() {
    val sheetState = rememberModalBottomSheetState()

    PeekrAppTheme {
        ReportResultModal(
            sheetState = sheetState,
            isSuccess = true,
            onDismissRequest = {},
            onCancel = {},
            onFinishClick = {},
        )
    }
}
