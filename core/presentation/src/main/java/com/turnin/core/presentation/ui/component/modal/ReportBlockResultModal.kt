package com.turnin.core.presentation.ui.component.modal

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.turnin.core.designsystem.component.button.PeekrButtonStyle
import com.turnin.core.designsystem.component.button.PeekrSolidButton
import com.turnin.core.designsystem.component.modal.PeekrModalBottomSheet
import com.turnin.core.designsystem.theme.PeekrAppTheme
import com.turnin.core.designsystem.theme.PeekrTheme
import com.turnin.core.presentation.ui.util.UiText

/**
 * 신고/차단 결과 모달
 *
 * @param modifier [Modifier]
 * @param sheetState [SheetState]
 * @param error 에러 메시지
 * @param errorBtnText 에러 발생 시 버튼 텍스트
 * @param normalBtnText 기본 버튼 텍스트
 * @param successTitle 성공 시(정상 결과) 표시할 타이틀
 * @param successContent 성공 시(정상 결과) 표시할 내용
 * @param onDismissRequest 모달이 사라질 때 수행할 콜백
 * @param onFinishClick 완료 클릭 시 콜백
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportBlockResultModal(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    error: UiText?,
    errorBtnText: String,
    normalBtnText: String,
    successTitle: String,
    successContent: String,
    onDismissRequest: () -> Unit,
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
            error = error?.asString(),
            errorBtnText = errorBtnText,
            normalBtnText = normalBtnText,
            successTitle = successTitle,
            successContent = successContent,
            onFinishClick = onFinishClick,
        )
    }
}

/**
 * 내부 컨텐츠
 *
 * @param modifier [Modifier]
 * @param error 에러 메시지
 * @param onFinishClick 완료 클릭 시 콜백
 */
@Composable
private fun Content(
    modifier: Modifier = Modifier,
    error: String?,
    errorBtnText: String,
    normalBtnText: String,
    successTitle: String,
    successContent: String,
    onFinishClick: () -> Unit,
) {
    val buttonText = if (error != null) errorBtnText else normalBtnText

    val alignment = if (error != null) {
        Alignment.Center
    } else {
        Alignment.TopCenter
    }

    Column(modifier) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = alignment,
        ) {
            if (error != null) {
                // 에러 발생 시
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = error,
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
                        text = successTitle,
                        style = PeekrTheme.typography.headline3,
                        fontWeight = FontWeight.Medium,
                        color = PeekrTheme.colorScheme.textNormal,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = successContent,
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

// ------------------------------ Previews ------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "정상 완료")
@Composable
private fun ReportBlockResultModalPreview() {
    PeekrAppTheme {
        ReportBlockResultModal(
            modifier = Modifier,
            sheetState = rememberModalBottomSheetState(),
            error = null,
            errorBtnText = "확인",
            normalBtnText = "완료",
            successTitle = "신고/차단 완료",
            successContent = "신고/차단을 완료했습니다.",
            onDismissRequest = {},
            onFinishClick = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "에러 발생 시")
@Composable
private fun ReportBlockResultModalErrorPreview() {
    PeekrAppTheme {
        ReportBlockResultModal(
            modifier = Modifier,
            sheetState = rememberModalBottomSheetState(),
            error = UiText.DynamicString("에러 발생 문구"),
            errorBtnText = "확인",
            normalBtnText = "완료",
            successTitle = "신고/차단 완료",
            successContent = "신고/차단을 완료했습니다.",
            onDismissRequest = {},
            onFinishClick = {},
        )
    }
}
