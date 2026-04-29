package com.turnin.core.designsystem.component.button

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import com.turnin.core.designsystem.component.icon.PeekrIconSize
import com.turnin.core.designsystem.theme.PeekrTheme
import com.turnin.core.designsystem.util.click.ClickMode
import com.turnin.core.designsystem.util.click.clickableSingle
import com.turnin.core.designsystem.util.icon.PeekrIconType

/**
 * Peekr Icon Button
 *
 * @param icon [PeekrIconType]
 * @param iconSize [PeekrIconSize]
 * @param contentDescription 아이콘 설명
 * @param modifier [Modifier]
 * @param enabled 아이콘 활성화 여부
 * @param expandedTouchTarget `true`면 터치타겟이 레이아웃 영역 내에 포함되고, `false`면 포함되지 않는다.
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
    expandedTouchTarget: Boolean = true,
    tint: Color = if (enabled) {
        PeekrTheme.colorScheme.textNormal
    } else {
        PeekrTheme.colorScheme.interactionDisable
    },
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .size(
                if (expandedTouchTarget) {
                    iconSize.touchTargetRadius * 2
                } else {
                    iconSize.size
                },
            )
            .clip(CircleShape)
            .clickableSingle(
                clickMode = ClickMode.Throttle,
                enabled = enabled,
                onClick = onClick,
                indication = ripple(
                    bounded = !expandedTouchTarget,
                    radius = iconSize.touchTargetRadius,
                ),
            )
            .semantics { role = Role.Button }
            .then(
                if (expandedTouchTarget) {
                    Modifier
                } else {
                    Modifier.minimumInteractiveComponentSize()
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier.size(iconSize.size),
            imageVector = icon.imageVector,
            contentDescription = contentDescription,
            tint = tint,
        )
    }
}
