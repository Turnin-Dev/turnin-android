package com.peekr.core.designsystem.component.fab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.component.icon.PeekrIcon
import com.peekr.core.designsystem.component.icon.PeekrIconSize
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.PeekrShadowType
import com.peekr.core.designsystem.util.click.clickableSingle
import com.peekr.core.designsystem.util.icon.PeekrIconType
import com.peekr.core.designsystem.util.icon.PeekrIcons
import com.peekr.core.designsystem.util.icon.Plus
import com.peekr.core.designsystem.util.peekrShadow

/**
 * Peekr Floating Action Button
 *
 * @param modifier [Modifier]
 * @param icon 아이콘
 * @param contentDescription Fab 기능 설명
 * @param enabled 활성화 여부
 * @param text 텍스트
 * @param shape Fab 모양
 * @param onClick 클릭 시 콜백
 */
@Composable
fun PeekrFab(
    modifier: Modifier = Modifier,
    icon: PeekrIconType,
    contentDescription: String?,
    enabled: Boolean = true,
    text: String? = null,
    shape: Shape = FabShape,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .peekrShadow(PeekrShadowType.Normal, shape)
            .clip(shape)
            .background(
                if (enabled) {
                    PeekrTheme.colorScheme.primary
                } else {
                    PeekrTheme.colorScheme.interactionDisable
                },
            )
            .clickableSingle(onClick = onClick, enabled = enabled),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            PeekrIcon(
                icon = icon,
                iconSize = PeekrIconSize.Normal,
                contentDescription = contentDescription,
                tint = PeekrTheme.colorScheme.staticWhite,
            )
            text?.let {
                Text(
                    text = it,
                    style = PeekrTheme.typography.caption3,
                    fontWeight = FontWeight.Medium,
                    color = PeekrTheme.colorScheme.staticWhite,
                )
            }
        }
    }
}

private val FabShape = RoundedCornerShape(14.dp)

@Preview
@Composable
private fun PeekrFabPreview() {
    PeekrAppTheme {
        Row(
            modifier = Modifier.background(Color.White),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PeekrFab(
                modifier = Modifier.size(50.dp),
                icon = PeekrIcons.Default.Bold.Plus,
                contentDescription = null,
                onClick = {},
            )
            PeekrFab(
                modifier = Modifier.size(50.dp),
                icon = PeekrIcons.Default.Bold.Plus,
                contentDescription = null,
                shape = CircleShape,
                onClick = {},
            )
            PeekrFab(
                modifier = Modifier.size(50.dp),
                icon = PeekrIcons.Default.Bold.Plus,
                text = "추가",
                contentDescription = null,
                shape = CircleShape,
                onClick = {},
            )
            PeekrFab(
                modifier = Modifier.size(50.dp),
                icon = PeekrIcons.Default.Bold.Plus,
                contentDescription = null,
                enabled = false,
                onClick = {},
            )
        }
    }
}
