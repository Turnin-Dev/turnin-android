package com.peekr.core.presentation.keyword.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.PeekrShadowType
import com.peekr.core.designsystem.util.click.clickableSingle
import com.peekr.core.designsystem.util.peekrShadow
import com.peekr.core.presentation.util.PreviewLightDarkWithBackground

/**
 * 키워드 노드 컴포넌트
 *
 * @param modifier [Modifier]
 * @param label 키워드 (이름)
 * @param onClick 키워드 클릭시
 * @param onLongClick 키워드 길게 클릭시
 */
@Composable
fun KeywordNode(
    modifier: Modifier = Modifier,
    label: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .peekrShadow(type = PeekrShadowType.Normal, shape = Shape)
            .clip(Shape)
            .background(PeekrTheme.colorScheme.backgroundNormal)
            .clickableSingle(
                onClick = onClick,
                onLongClick = onLongClick,
            )
            .padding(horizontal = HorizontalPaddingDp, vertical = VerticalPaddingDp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = PeekrTheme.typography.caption1,
            fontWeight = FontWeight.Normal,
            color = PeekrTheme.colorScheme.textNormal,
        )
    }
}

private val HorizontalPaddingDp = 20.dp
private val VerticalPaddingDp = 8.dp
private val Shape = RoundedCornerShape(14.dp)

@PreviewLightDarkWithBackground
@Composable
private fun KeywordNodePreview() {
    PeekrAppTheme {
        KeywordNode(
            label = "Label",
            onClick = {},
            onLongClick = {},
        )
    }
}
