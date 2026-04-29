package com.turnin.core.designsystem.component.chip

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.turnin.core.designsystem.component.icon.PeekrIconSize
import com.turnin.core.designsystem.theme.PeekrTheme
import com.turnin.core.designsystem.util.click.ClickMode
import com.turnin.core.designsystem.util.click.clickableSingle
import com.turnin.core.designsystem.util.icon.PeekrIconType

/**
 * PeekrChip
 *
 * @param text 칩 텍스트
 * @param onClick 칩 클릭 시
 * @param modifier [Modifier]
 * @param color 칩 색상
 * @param icon 칩 아이콘
 */
@Composable
fun PeekrChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = PeekrTheme.colorScheme.backgroundAssist,
    icon: PeekrIconType? = null,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(color)
            .clickableSingle(
                clickMode = ClickMode.Throttle,
                onClick = onClick,
            )
            .padding(horizontal = 16.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon?.let {
                Icon(
                    modifier = Modifier.size(PeekrIconSize.Tiny.size),
                    imageVector = icon.imageVector,
                    contentDescription = text,
                    tint = PeekrTheme.colorScheme.textNormal,
                )
            }
            Text(
                text = text,
                style = PeekrTheme.typography.caption2,
                fontWeight = FontWeight.Normal,
                color = PeekrTheme.colorScheme.textNormal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
