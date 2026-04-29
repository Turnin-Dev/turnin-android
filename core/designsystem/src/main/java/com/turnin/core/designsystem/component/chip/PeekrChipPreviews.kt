package com.turnin.core.designsystem.component.chip

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.turnin.core.designsystem.theme.PeekrAppTheme
import com.turnin.core.designsystem.util.icon.PeekrIcons
import com.turnin.core.designsystem.util.icon.Profile

@PreviewLightDark
@Composable
private fun PeekrChipPreview1() {
    PeekrAppTheme {
        PeekrChip(text = "Label", onClick = {})
    }
}

@PreviewLightDark
@Composable
private fun PeekrChipPreview2() {
    PeekrAppTheme {
        PeekrChip(text = "Label", icon = PeekrIcons.Filled.Normal.Profile, onClick = {})
    }
}
