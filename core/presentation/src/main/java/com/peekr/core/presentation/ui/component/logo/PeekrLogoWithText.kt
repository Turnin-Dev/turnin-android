package com.peekr.core.presentation.ui.component.logo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.component.logo.PeekrLogo
import com.peekr.core.designsystem.component.logo.PeekrLogoType

/**
 * Peekr의 아이콘 로고와 텍스트 로고가 함께 있는 버전
 *
 * @param modifier [Modifier]
 * @param iconWidthSize 아이콘 로고의 가로 사이즈 (내부적으로 `가로 길이 * 비율 = 세로 길이`가 계산 된다.)
 * @param textWidthSize 텍스트 로고의 가로 사이즈 (내부적으로 `가로 길이 * 비율 = 세로 길이`가 계산 된다.)
 */
@Composable
fun PeekrLogoWithText(
    modifier: Modifier = Modifier,
    iconWidthSize: Int = 71,
    textWidthSize: Int = 89,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        PeekrLogo(
            logoType = PeekrLogoType.Icon,
            logoWidth = 71,
        )
        Spacer(Modifier.height(13.dp))
        PeekrLogo(
            logoType = PeekrLogoType.Text,
            logoWidth = 89,
        )
    }
}

@Preview
@Composable
private fun PeekrLogoWithTextPreview() {
    PeekrLogoWithText()
}
