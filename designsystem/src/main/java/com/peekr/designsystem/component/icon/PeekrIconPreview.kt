package com.peekr.designsystem.component.icon

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.peekr.designsystem.util.icon.Home
import com.peekr.designsystem.util.icon.PeekrIcons

@Preview(showBackground = true)
@Composable
private fun Icon_Size() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        PeekrIconSize.entries.forEach { iconSize ->
            PeekrIcon(
                icon = PeekrIcons.Filled.Home,
                iconSize = iconSize,
                contentDescription = "",
            )
        }
    }
}
