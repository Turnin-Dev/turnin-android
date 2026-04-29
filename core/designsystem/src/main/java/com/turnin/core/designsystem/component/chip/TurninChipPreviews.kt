package com.turnin.core.designsystem.component.chip

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.turnin.core.designsystem.theme.TurninAppTheme
import com.turnin.core.designsystem.util.icon.Profile
import com.turnin.core.designsystem.util.icon.TurninIcons

@PreviewLightDark
@Composable
private fun TurninChipPreview1() {
    TurninAppTheme {
        TurninChip(text = "Label", onClick = {})
    }
}

@PreviewLightDark
@Composable
private fun TurninChipPreview2() {
    TurninAppTheme {
        TurninChip(text = "Label", icon = TurninIcons.Filled.Normal.Profile, onClick = {})
    }
}
