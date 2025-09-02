package com.peekr.presentation.keyword.common.view

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.peekr.designsystem.theme.PeekrAppTheme
import com.peekr.designsystem.theme.PeekrTheme
import com.peekr.designsystem.util.PeekrShadowType
import com.peekr.designsystem.util.click.clickableSingle
import com.peekr.designsystem.util.peekrShadow

/**
 * 키워드 컴포넌트
 *
 * @param modifier [Modifier]
 * @param label 키워드 (이름)
 * @param onClick 키워드 클릭시
 */
@Composable
fun Keyword(
    modifier: Modifier = Modifier,
    label: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(Shape)
            .peekrShadow(type = PeekrShadowType.Normal, shape = Shape)
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

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun KeywordPreview() {
    PeekrAppTheme {
        Box(Modifier.size(80.dp), Alignment.Center) {
            Keyword(
                label = "Label",
                onClick = { },
            )
        }
    }
}
