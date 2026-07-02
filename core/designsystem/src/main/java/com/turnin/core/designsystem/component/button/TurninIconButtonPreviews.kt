package com.turnin.core.designsystem.component.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.turnin.core.designsystem.component.icon.TurninIconSize
import com.turnin.core.designsystem.theme.TurninAppTheme
import com.turnin.core.designsystem.util.icon.Home
import com.turnin.core.designsystem.util.icon.TurninIcons

@Preview(showBackground = true)
@Composable
private fun IconButton_Size() {
    TurninAppTheme {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TurninIconSize.entries.forEach { iconSize ->
                TurninIconButton(
                    icon = TurninIcons.Filled.Normal.Home,
                    iconSize = iconSize,
                    contentDescription = "",
                    onClick = { },
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun IconButton_Expanded_TouchTarget_False() {
    TurninAppTheme {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TurninIconSize.entries.forEach { iconSize ->
                TurninIconButton(
                    icon = TurninIcons.Filled.Normal.Home,
                    iconSize = iconSize,
                    contentDescription = "",
                    expandedTouchTarget = false,
                    onClick = { },
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun IconButton_Disabled() {
    TurninAppTheme {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TurninIconSize.entries.forEach { iconSize ->
                TurninIconButton(
                    icon = TurninIcons.Filled.Normal.Home,
                    iconSize = iconSize,
                    contentDescription = "",
                    enabled = false,
                    onClick = { },
                )
            }
        }
    }
}
