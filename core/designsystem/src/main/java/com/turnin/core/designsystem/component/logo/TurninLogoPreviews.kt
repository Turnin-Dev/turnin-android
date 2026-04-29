package com.turnin.core.designsystem.component.logo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.turnin.core.designsystem.theme.TurninAppTheme

@Preview
@Composable
private fun TurninLogoPreview() {
    TurninAppTheme {
        Column(verticalArrangement = Arrangement.spacedBy(25.dp)) {
            TurninLogo(
                logoType = TurninLogoType.Default,
                logoWidth = 150,
            )
            TurninLogo(
                logoType = TurninLogoType.Text,
                logoWidth = 300,
            )
            TurninLogo(
                logoType = TurninLogoType.Icon,
                logoWidth = 300,
            )
        }
    }
}
