package com.turnin.core.designsystem.component.fab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
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
import com.turnin.core.designsystem.component.icon.TurninIcon
import com.turnin.core.designsystem.component.icon.TurninIconSize
import com.turnin.core.designsystem.theme.TurninAppTheme
import com.turnin.core.designsystem.theme.TurninTheme
import com.turnin.core.designsystem.util.TurninShadowType
import com.turnin.core.designsystem.util.click.clickableSingle
import com.turnin.core.designsystem.util.icon.Plus
import com.turnin.core.designsystem.util.icon.TurninIconType
import com.turnin.core.designsystem.util.icon.TurninIcons
import com.turnin.core.designsystem.util.turninShadow

/**
 * Turnin Floating Action Button
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
fun TurninFab(
    modifier: Modifier = Modifier,
    icon: TurninIconType,
    contentDescription: String?,
    enabled: Boolean = true,
    text: String? = null,
    shape: Shape = FabShape,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .turninShadow(TurninShadowType.Normal, shape)
            .clip(shape)
            .background(
                if (enabled) {
                    TurninTheme.colorScheme.primary
                } else {
                    TurninTheme.colorScheme.interactionDisable
                },
            )
            .clickableSingle(onClick = onClick, enabled = enabled),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier.padding(
                vertical = 14.dp,
                horizontal = if (text == null) 14.dp else 22.dp,
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TurninIcon(
                icon = icon,
                iconSize = TurninIconSize.Normal,
                contentDescription = contentDescription,
                tint = TurninTheme.colorScheme.staticWhite,
            )
            text?.let {
                Text(
                    text = it,
                    style = TurninTheme.typography.caption2,
                    fontWeight = FontWeight.Medium,
                    color = TurninTheme.colorScheme.staticWhite,
                )
            }
        }
    }
}

private val FabShape = RoundedCornerShape(14.dp)

@Preview
@Composable
private fun TurninFabPreview() {
    TurninAppTheme {
        Row(
            modifier = Modifier.background(Color.White),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TurninFab(
                icon = TurninIcons.Default.Bold.Plus,
                contentDescription = null,
                onClick = {},
            )
            TurninFab(
                icon = TurninIcons.Default.Bold.Plus,
                contentDescription = null,
                shape = CircleShape,
                onClick = {},
            )
            TurninFab(
                icon = TurninIcons.Default.Bold.Plus,
                text = "추가",
                contentDescription = null,
                shape = CircleShape,
                onClick = {},
            )
            TurninFab(
                icon = TurninIcons.Default.Bold.Plus,
                contentDescription = null,
                enabled = false,
                onClick = {},
            )
        }
    }
}
