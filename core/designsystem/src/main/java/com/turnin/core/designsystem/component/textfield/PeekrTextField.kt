package com.turnin.core.designsystem.component.textfield

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.turnin.core.designsystem.theme.PeekrAppTheme
import com.turnin.core.designsystem.theme.PeekrTheme

/**
 * PeekrTextField
 *
 * @param text 텍스트
 * @param onTextChanged 텍스트 변화 시 콜백
 * @param placeholder 텍스트가 비어있을 때 표시할 자리표시자
 * @param modifier [Modifier]
 * @param singleLine 한 줄 제한 여부
 * @param isError 에러 발생 여부
 * @param readOnly 읽기 전용 여부
 * @param supportingText 보조 텍스트로 보통 에러 메시지를 표시하는 데 사용한다. (텍스트 필드 하단에 위치)
 */
@Composable
fun PeekrTextField(
    text: String,
    onTextChanged: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = false,
    isError: Boolean = false,
    readOnly: Boolean = false,
    supportingText: (@Composable () -> Unit)? = null,
) {
    CoreTextField(
        text = text,
        onTextChanged = onTextChanged,
        placeholder = placeholder,
        modifier = modifier,
        singleLine = singleLine,
        isError = isError,
        supportingText = supportingText,
        readOnly = readOnly,
    )
}

/**
 * CoreTextField
 *
 * @param text 텍스트
 * @param onTextChanged 텍스트 변화 시 콜백
 * @param placeholder 텍스트가 비어있을 때 표시할 자리표시자
 * @param modifier [Modifier]
 * @param singleLine 한 줄 제한 여부
 * @param isError 에러 발생 여부
 * @param supportingText 보조 텍스트로 보통 에러 메시지를 표시하는 데 사용한다. (텍스트 필드 하단에 위치)
 */
@Composable
private fun CoreTextField(
    text: String,
    onTextChanged: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = false,
    isError: Boolean = false,
    readOnly: Boolean = false,
    supportingText: (@Composable () -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focused = interactionSource.collectIsFocusedAsState().value
    val colorScheme = PeekrTheme.colorScheme
    val color = remember(isError, text.isEmpty(), focused, colorScheme) {
        when {
            isError -> colorScheme.statusNegative
            text.isEmpty() && !focused -> colorScheme.textPlaceholder
            else -> colorScheme.textNormal
        }
    }
    val lineColor = remember(isError, focused) {
        when {
            isError -> colorScheme.statusNegative
            focused -> colorScheme.textNormal
            else -> colorScheme.textPlaceholder
        }
    }

    BasicTextField(
        modifier = modifier,
        value = text,
        onValueChange = { onTextChanged(it) },
        textStyle = PeekrTheme.typography.body1.copy(
            color = color,
            fontWeight = FontWeight.Medium,
        ),
        singleLine = singleLine,
        interactionSource = interactionSource,
        cursorBrush = SolidColor(color),
        readOnly = readOnly,
    ) { innerTextField ->
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
        ) {
            Spacer(Modifier.size(DefaultGapDp))
            Box(contentAlignment = Alignment.CenterStart) {
                if (text.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = PeekrTheme.typography.body1.copy(fontWeight = FontWeight.Medium),
                        color = PeekrTheme.colorScheme.textPlaceholder,
                    )
                }
                innerTextField()
            }
            Spacer(Modifier.size(DefaultGapDp))
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 0.6.dp,
                color = lineColor,
            )
            if (supportingText != null) {
                Spacer(Modifier.size(SubGapDp))
                supportingText()
            }
        }
    }
}

/**
 * [CoreTextField]의 `supportingText` 부분에서 사용한다.
 */
@Composable
fun PeekrSupportingText(
    text: String,
    color: Color = PeekrTheme.colorScheme.textNormal,
    maxLines: Int = 2,
) {
    Text(
        text = text,
        style = PeekrTheme.typography.label2,
        color = color,
        maxLines = maxLines,
    )
}

private val DefaultGapDp = 12.dp
private val SubGapDp = 6.dp

@Preview(showBackground = true)
@Composable
private fun PeekrTextFieldPreview() {
    val (text, onTextChanged) = remember { mutableStateOf("") }
    val (text2, onText2Changed) = remember { mutableStateOf("") }
    var isError1 by remember { mutableStateOf(false) }
    var isError2 by remember { mutableStateOf(false) }
    var errorMessage: String? by remember { mutableStateOf(null) }

    LaunchedEffect(text, text2) {
        if (text.length > 5) {
            isError1 = true
            errorMessage = "길이는 5글자를 초과할 수 없어요"
        } else {
            isError1 = false
            errorMessage = null
        }
        isError2 = text2.length > 5
    }

    PeekrAppTheme {
        Column(
            Modifier
                .fillMaxSize()
                .background(Color.LightGray)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            PeekrTextField(
                modifier = Modifier.background(Color.White.copy(0.5f)),
                text = text,
                onTextChanged = onTextChanged,
                placeholder = "Placeholder",
                isError = isError1,
                supportingText = {
                    PeekrSupportingText(
                        text = errorMessage ?: "",
                        color = PeekrTheme.colorScheme.statusNegative,
                    )
                },
            )
            PeekrTextField(
                modifier = Modifier.background(Color.White.copy(0.5f)),
                text = text2,
                onTextChanged = onText2Changed,
                placeholder = "Placeholder",
                isError = isError2,
            )
            PeekrTextField(
                modifier = Modifier.background(Color.White.copy(0.5f)),
                text = "Fixed Text",
                onTextChanged = {},
                placeholder = "Placeholder",
                isError = false,
                readOnly = true,
            )
        }
    }
}
