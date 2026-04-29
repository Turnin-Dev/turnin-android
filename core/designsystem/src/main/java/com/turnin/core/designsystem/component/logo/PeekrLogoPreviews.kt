package com.turnin.core.designsystem.component.logo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.turnin.core.designsystem.theme.PeekrAppTheme

@Preview
@Composable
private fun PeekrLogoPreview() {
    PeekrAppTheme {
        Column(verticalArrangement = Arrangement.spacedBy(25.dp)) {
            PeekrLogo(
                logoType = PeekrLogoType.Default,
                logoWidth = 150,
            )
            PeekrLogo(
                logoType = PeekrLogoType.Text,
                logoWidth = 300,
            )
            PeekrLogo(
                logoType = PeekrLogoType.Icon,
                logoWidth = 300,
            )
        }
    }
}
