package com.peekr.core.presentation.keyword.view

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.PeekrShadowType
import com.peekr.core.designsystem.util.click.clickableSingle
import com.peekr.core.designsystem.util.peekrShadow

/**
 * 키워드 노드 컴포넌트
 *
 * @param modifier [Modifier]
 * @param label 키워드 (이름)
 * @param onClick 키워드 클릭시
 */
@Composable
fun KeywordNode(
    modifier: Modifier = Modifier,
    label: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .peekrShadow(type = PeekrShadowType.Normal, shape = Shape)
            .clip(Shape)
            .background(PeekrTheme.colorScheme.backgroundNormal)
            .clickableSingle(onClick = onClick)
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

@Preview(showBackground = true, widthDp = 300, heightDp = 300, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showBackground = true, widthDp = 300, heightDp = 300, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun KeywordNodePreview() {
    PeekrAppTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.White),
            Alignment.Center,
        ) {
            KeywordNode(
                label = "Label",
                onClick = { },
            )
        }
    }
}
