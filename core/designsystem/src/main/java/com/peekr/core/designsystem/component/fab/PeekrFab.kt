package com.peekr.core.designsystem.component.fab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.component.icon.PeekrIcon
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.click.clickableSingle
import com.peekr.core.designsystem.util.icon.PeekrIcons
import com.peekr.core.designsystem.util.icon.Plus

/**
 * Peekr Floating Action Button
 */
@Composable
fun PeekrFab(
    modifier: Modifier = Modifier,
    contentDescription: String?,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(FabShape)
            .background(PeekrTheme.colorScheme.primary)
            .clickableSingle(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        PeekrIcon(
            modifier = Modifier.size(24.dp),
            icon = PeekrIcons.Default.Bold.Plus,
            contentDescription = contentDescription,
            tint = PeekrTheme.colorScheme.staticWhite,
        )
    }
}

private val FabShape = RoundedCornerShape(14.dp)

@Preview
@Composable
private fun PeekrFabPreview() {
    PeekrAppTheme {
        PeekrFab(
            modifier = Modifier.size(50.dp),
            contentDescription = null,
            onClick = {},
        )
    }
}
