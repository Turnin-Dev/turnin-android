package com.turnin.core.designsystem.component.modal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.turnin.core.designsystem.R
import com.turnin.core.designsystem.theme.TurninAppTheme
import com.turnin.core.designsystem.theme.TurninTheme
import com.turnin.core.designsystem.util.click.clickableSingle

typealias ModalText = String
typealias ModalTextColor = Color
typealias OnModalTextClick = () -> Unit

/**
 * [TurninModalBottomSheetContent]에서 사용하는 토큰으로 순서대로
 * (텍스트, 컬러, 항목 클릭 시 콜백)이다.
 */
typealias ModalContentToken = Triple<ModalText, ModalTextColor, OnModalTextClick>

/**
 * [TurninModalBottomSheet] 내부에서 사용하는 컨텐츠
 *
 * 기본적으로 취소 항목은 활성화 되어있다.
 *
 * @param modifier [Modifier]
 * @param onCancel 취소 항목 클릭 시
 * @param token [ModalContentToken] 모달 항목 토큰 (여러 개 허용)
 *
 * @sample TurninModalBottomSheetContentPreview
 */
@Composable
fun TurninModalBottomSheetContent(
    modifier: Modifier = Modifier,
    onCancel: () -> Unit,
    vararg token: ModalContentToken,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        token.forEach { (text, color, onClick) ->
            Content(
                text = text,
                color = color,
                onClick = onClick,
            )
        }
        ContentDivider(Modifier.fillMaxWidth())
        Content(
            text = stringResource(R.string.bottom_modal_sheet_content_cancel),
            color = TurninTheme.colorScheme.textNormal,
            onClick = onCancel,
        )
    }
}

@Composable
private fun ContentDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier,
        thickness = 0.5.dp,
        color = TurninTheme.colorScheme.lineNormal,
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    text: String,
    color: Color,
    onClick: () -> Unit,
) {
    Text(
        modifier = modifier
            .fillMaxWidth()
            .clickableSingle(onClick = onClick)
            .padding(10.dp),
        text = text,
        style = TurninTheme.typography.body2,
        fontWeight = FontWeight.Medium,
        color = color,
        textAlign = TextAlign.Center,
    )
}

// ------------------------------ Previews ------------------------------
@Preview
@Composable
private fun ContentPreview() {
    Content(
        text = "Label 1",
        color = Color.Black,
        onClick = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun TurninModalBottomSheetContentPreview() {
    TurninAppTheme {
        TurninModalBottomSheetContent(
            modifier = Modifier.fillMaxWidth(),
            onCancel = {},
            ModalContentToken("Label 1", TurninTheme.colorScheme.textNormal, {}),
            ModalContentToken("Label 2", TurninTheme.colorScheme.textNormal, {}),
            ModalContentToken("Label 3", TurninTheme.colorScheme.statusNegative, {}),
        )
    }
}
