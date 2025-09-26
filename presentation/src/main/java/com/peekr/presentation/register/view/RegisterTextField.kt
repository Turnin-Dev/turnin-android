package com.peekr.presentation.register.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.presentation.modifier.accessibility

/**
 * 회원가입 화면에서 사용하는 텍스트 필드
 *
 * @param text 입력할 텍스트
 * @param onTextChanged 입력할 텍스트 콜백
 * @param placeholder 자리 표시자
 * @param modifier [Modifier]
 * @param errorMessage 에러 메시지
 */
@Composable
fun RegisterTextField(
    text: String,
    onTextChanged: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    errorMessage: String?,
    singleLine: Boolean,
) {
    val isError = errorMessage != null && errorMessage.isNotEmpty()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        BaseTextField(
            text = text,
            onTextChanged = onTextChanged,
            placeholder = placeholder,
            modifier = Modifier.fillMaxWidth(),
            isError = isError,
            singleLine = singleLine,
        )
        if (isError) {
            ErrorMessage(
                errorMessage = errorMessage,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/**
 * 회원가입 화면에서 사용하는 커스텀 텍스트 필드
 *
 * @param text 입력할 텍스트
 * @param onTextChanged 입력할 텍스트 콜백
 * @param placeholder 자리 표시자
 * @param modifier [Modifier]
 * @param isError 에러 발생 여부
 */
@Composable
private fun BaseTextField(
    text: String,
    onTextChanged: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    singleLine: Boolean,
) {
    BasicTextField(
        modifier = modifier,
        value = text,
        onValueChange = { onTextChanged(it) },
        textStyle = PeekrTheme.typography.headline2.copy(
            color = if (isError) PeekrTheme.colorScheme.statusNegative else PeekrTheme.colorScheme.textNormal,
            fontWeight = FontWeight.Medium,
        ),
        singleLine = singleLine,
    ) { innerTextField ->
        Box(contentAlignment = Alignment.CenterStart) {
            if (text.isEmpty()) {
                Text(
                    text = placeholder,
                    style = PeekrTheme.typography.headline2.copy(fontWeight = FontWeight.Medium),
                    color = PeekrTheme.colorScheme.textPlaceholder,
                )
            }
            innerTextField()
        }
    }
}

/**
 * 텍스트 필드에 하단에 표시하는 에러 메시지
 *
 * @param errorMessage 에러 메시지
 * @param modifier [Modifier]
 */
@Composable
private fun ErrorMessage(
    errorMessage: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier.accessibility(errorMessage),
        text = errorMessage,
        style = PeekrTheme.typography.caption1,
        color = PeekrTheme.colorScheme.statusNegative,
        textAlign = TextAlign.Start,
    )
}

@Preview(name = "기본 상태", showBackground = true)
@Composable
private fun RegisterTextFieldPreview() {
    var (text, onTextChanged) = remember { mutableStateOf("") }

    PeekrAppTheme {
        RegisterTextField(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            text = text,
            onTextChanged = onTextChanged,
            placeholder = "이름",
            errorMessage = null,
            singleLine = true,
        )
    }
}

@Preview(name = "에러 발생", showBackground = true)
@Composable
private fun RegisterTextFieldPreview2() {
    var (text, onTextChanged) = remember { mutableStateOf("닉!") }

    PeekrAppTheme {
        RegisterTextField(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            text = text,
            onTextChanged = onTextChanged,
            placeholder = "이름2",
            errorMessage = "공백 포함, 3 ~ 15글자, 특수기호(@, !, ., _, -)만 가능합니다.",
            singleLine = true,
        )
    }
}

@Preview(name = "BaseTextField", showBackground = true)
@Composable
private fun BaseTextFieldPreview() {
    var (text, onTextChanged) = remember { mutableStateOf("") }

    PeekrAppTheme {
        BaseTextField(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            text = text,
            onTextChanged = onTextChanged,
            placeholder = "이름3",
            isError = false,
            singleLine = true,
        )
    }
}
