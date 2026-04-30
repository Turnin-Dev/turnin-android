package com.turnin.core.designsystem.component.icon

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.turnin.core.designsystem.theme.TurninTheme
import com.turnin.core.designsystem.util.icon.TurninIconType

/**
 * Turnin Icon
 *
 * @param icon 아이콘
 * @param contentDescription 아이콘 설명
 * @param modifier [Modifier]
 * @param iconSize 아이콘 사이즈
 * @param tint 아이콘 색상
 *
 * @see TurninIconType
 * @see TurninIconSize
 */
@Composable
fun TurninIcon(
    icon: TurninIconType,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    iconSize: TurninIconSize = TurninIconSize.Normal,
    tint: Color = TurninTheme.colorScheme.textNormal,
) {
    Icon(
        modifier = modifier.size(iconSize.size),
        imageVector = icon.imageVector,
        contentDescription = contentDescription,
        tint = tint,
    )
}
