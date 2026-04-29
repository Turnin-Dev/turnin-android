package com.turnin.core.designsystem.component.loading

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.turnin.core.designsystem.theme.PeekrTheme

@Composable
fun PeekrLoadingIndicator(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        modifier = modifier.size(30.dp),
        color = PeekrTheme.colorScheme.primary,
    )
}
