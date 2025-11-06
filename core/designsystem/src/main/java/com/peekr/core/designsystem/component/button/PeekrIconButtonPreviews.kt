package com.peekr.core.designsystem.component.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.component.icon.PeekrIconSize
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.util.icon.Home
import com.peekr.core.designsystem.util.icon.PeekrIcons

@Preview(showBackground = true)
@Composable
private fun IconButton_Size() {
    PeekrAppTheme {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PeekrIconSize.entries.forEach { iconSize ->
                PeekrIconButton(
                    icon = PeekrIcons.Filled.Normal.Home,
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
    PeekrAppTheme {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PeekrIconSize.entries.forEach { iconSize ->
                PeekrIconButton(
                    icon = PeekrIcons.Filled.Normal.Home,
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
    PeekrAppTheme {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PeekrIconSize.entries.forEach { iconSize ->
                PeekrIconButton(
                    icon = PeekrIcons.Filled.Normal.Home,
                    iconSize = iconSize,
                    contentDescription = "",
                    enabled = false,
                    onClick = { },
                )
            }
        }
    }
}
