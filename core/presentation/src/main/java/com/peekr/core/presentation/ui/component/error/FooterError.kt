package com.peekr.core.presentation.ui.component.error

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.click.clickableSingle
import com.peekr.core.presentation.R
import com.peekr.core.presentation.ui.util.PreviewLightDarkWithBackground

/**
 * 에러 발생 시 목록 하단에 표시할 footer
 *
 * @param modifier [Modifier]
 * @param errorMessage 에러 메시지
 * @param onRetry 재시도 로직
 */
@Composable
fun FooterError(
    modifier: Modifier = Modifier,
    errorMessage: String,
    onRetry: () -> Unit,
) {
    Column(
        modifier = modifier.heightIn(min = 78.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp, alignment = Alignment.CenterVertically),
    ) {
        Text(
            text = errorMessage,
            style = PeekrTheme.typography.label1,
            fontWeight = FontWeight.Medium,
            color = PeekrTheme.colorScheme.textAssist2,
        )
        Text(
            modifier = Modifier.clickableSingle(onClick = onRetry),
            text = stringResource(R.string.footer_error_retry),
            style = PeekrTheme.typography.label1,
            fontWeight = FontWeight.Bold,
            color = PeekrTheme.colorScheme.textStrong,
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun FooterErrorPreview() {
    PeekrAppTheme {
        FooterError(
            modifier = Modifier.fillMaxWidth(),
            errorMessage = "에러가 발생했어요.",
            onRetry = {},
        )
    }
}
