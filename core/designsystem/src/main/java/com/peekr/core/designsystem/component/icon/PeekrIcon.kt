package com.peekr.core.designsystem.component.icon

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.icon.PeekrIconType

/**
 * Peekr Icon
 *
 * @param icon 아이콘
 * @param contentDescription 아이콘 설명
 * @param modifier [Modifier]
 * @param iconSize 아이콘 사이즈
 * @param tint 아이콘 색상
 *
 * @see PeekrIconType
 * @see PeekrIconSize
 */
@Composable
fun PeekrIcon(
    icon: PeekrIconType,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    iconSize: PeekrIconSize = PeekrIconSize.Normal,
    tint: Color = PeekrTheme.colorScheme.textNormal,
) {
    Icon(
        modifier = modifier.size(iconSize.size),
        imageVector = icon.imageVector,
        contentDescription = contentDescription,
        tint = tint,
    )
}
