package com.peekr.presentation.keywordAdd.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.peekr.core.designsystem.theme.PeekrTheme

/**
 * 키워드 추가 화면에서 키워드 입력 시 사용하는 커스텀 텍스트 필드
 *
 * @param text 입력할 텍스트
 * @param onTextChanged 입력할 텍스트 콜백
 * @param placeholder 자리 표시자
 * @param modifier [Modifier]
 * @param isError 에러 발생 여부
 * @param readOnly 읽기 전용 여부
 */
@Composable
internal fun KeywordTextField(
    text: String,
    onTextChanged: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    readOnly: Boolean = false,
) {
    BasicTextField(
        modifier = modifier,
        value = text,
        onValueChange = { onTextChanged(it) },
        textStyle = PeekrTheme.typography.headline2.copy(
            color = if (isError) PeekrTheme.colorScheme.statusNegative else PeekrTheme.colorScheme.textNormal,
            fontWeight = FontWeight.Medium,
        ),
        maxLines = 2,
        readOnly = readOnly,
    ) { innerTextField ->
        Box(contentAlignment = Alignment.CenterStart) {
            if (text.isEmpty()) {
                Text(
                    text = placeholder,
                    style = PeekrTheme.typography.headline1.copy(fontWeight = FontWeight.Bold),
                    color = PeekrTheme.colorScheme.textPlaceholder,
                )
            }
            innerTextField()
        }
    }
}
