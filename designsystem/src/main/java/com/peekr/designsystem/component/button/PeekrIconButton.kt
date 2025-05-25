package com.peekr.designsystem.component.button

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.peekr.designsystem.component.icon.PeekrIconSize
import com.peekr.designsystem.theme.PeekrTheme
import com.peekr.designsystem.util.click.ClickMode
import com.peekr.designsystem.util.click.clickableSingle
import com.peekr.designsystem.util.icon.PeekrIconType

/**
 * Peekr Icon Button
 *
 * @param icon [PeekrIconType]
 * @param iconSize [PeekrIconSize]
 * @param contentDescription 아이콘 설명
 * @param modifier [Modifier]
 * @param enabled 아이콘 활성화 여부
 * @param tint 아이콘 색상
 * @param onClick 아이콘 클릭 시
 */
@Composable
fun PeekrIconButton(
    icon: PeekrIconType,
    iconSize: PeekrIconSize,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color = if (enabled) {
        PeekrTheme.colorScheme.textNormal
    } else {
        PeekrTheme.colorScheme.interactionDisable
    },
    onClick: () -> Unit,
) {
    Icon(
        modifier = Modifier
            .clip(CircleShape)
            .clickableSingle(
                clickMode = ClickMode.Throttle,
                enabled = enabled,
                onClick = { },
            ).padding(iconSize.getTouchTargetPadding())
            .size(iconSize.size),
        imageVector = icon.imageVector,
        contentDescription = contentDescription,
        tint = tint,
    )
}

private fun PeekrIconSize.getTouchTargetPadding(): Dp = when (this) {
    PeekrIconSize.Large -> 14.dp
    PeekrIconSize.Medium -> 12.dp
    PeekrIconSize.Normal -> 10.dp
    PeekrIconSize.Small -> 8.dp
    PeekrIconSize.Tiny -> 6.dp
}
