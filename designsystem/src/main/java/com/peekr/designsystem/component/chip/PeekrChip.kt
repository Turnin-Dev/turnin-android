package com.peekr.designsystem.component.chip

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
import androidx.compose.ui.unit.dp
import com.peekr.designsystem.component.icon.PeekrIconSize
import com.peekr.designsystem.theme.PeekrTheme
import com.peekr.designsystem.util.click.ClickMode
import com.peekr.designsystem.util.click.clickableSingle
import com.peekr.designsystem.util.icon.PeekrIconType

/**
 * PeekrChip
 *
 * @param text 칩 텍스트
 * @param onClick 칩 클릭 시
 * @param modifier [Modifier]
 * @param icon 칩 아이콘
 */
@Composable
fun PeekrChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: PeekrIconType? = null,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(PeekrTheme.colorScheme.backgroundAssist)
            .clickableSingle(
                clickMode = ClickMode.Throttle,
                onClick = onClick,
            ).padding(
                horizontal = if (icon == null) 15.dp else 10.dp,
                vertical = 4.dp,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
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
                style = PeekrTheme.typography.caption1,
                color = PeekrTheme.colorScheme.textNormal,
            )
        }
    }
}
