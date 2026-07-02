package com.turnin.core.designsystem.component.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.turnin.core.designsystem.theme.TurninAppTheme
import com.turnin.core.designsystem.util.icon.Arrow2Left
import com.turnin.core.designsystem.util.icon.TurninIcons
import kotlinx.coroutines.delay

@Preview(widthDp = 500)
@Composable
private fun SolidButton_Default() {
    TurninAppTheme {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TurninSolidButton(
                text = "Label",
                style = TurninButtonStyle.Large,
                onClick = {},
            )
            TurninSolidButton(
                text = "Label",
                style = TurninButtonStyle.Medium,
                onClick = {},
            )
            TurninSolidButton(
                text = "Label",
                style = TurninButtonStyle.Small,
                onClick = {},
            )
            TurninSolidButton(
                text = "Label",
                style = TurninButtonStyle.Tiny,
                onClick = {},
            )
        }
    }
}

@Preview(widthDp = 500)
@Composable
private fun SolidButton_Loading_Default() {
    var clicked by remember { mutableStateOf(false) }

    LaunchedEffect(clicked) {
        if (clicked) {
            delay(2000)
            clicked = false
        }
    }

    TurninAppTheme {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TurninSolidButton(
                text = "Label",
                style = TurninButtonStyle.Large,
                loading = clicked,
                onClick = { clicked = true },
            )
            TurninSolidButton(
                text = "Label",
                style = TurninButtonStyle.Medium,
                loading = true,
                onClick = {},
            )
            TurninSolidButton(
                text = "Label",
                style = TurninButtonStyle.Small,
                loading = true,
                onClick = {},
            )
            TurninSolidButton(
                text = "Label",
                style = TurninButtonStyle.Tiny,
                loading = true,
                onClick = {},
            )
        }
    }
}

@Preview(widthDp = 500)
@Composable
private fun SolidButton_Icon() {
    TurninAppTheme {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TurninSolidButton(
                text = "Label",
                style = TurninButtonStyle.Large,
                icon = TurninIcons.Default.Normal.Arrow2Left,
                onClick = {},
            )
            TurninSolidButton(
                text = "Label",
                style = TurninButtonStyle.Medium,
                icon = TurninIcons.Default.Normal.Arrow2Left,
                onClick = {},
            )
            TurninSolidButton(
                text = "Label",
                style = TurninButtonStyle.Small,
                icon = TurninIcons.Default.Normal.Arrow2Left,
                onClick = {},
            )
            TurninSolidButton(
                text = "Label",
                style = TurninButtonStyle.Tiny,
                icon = TurninIcons.Default.Normal.Arrow2Left,
                onClick = {},
            )
        }
    }
}

@Preview(widthDp = 500)
@Composable
private fun SolidButton_Disabled() {
    TurninAppTheme {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TurninSolidButton(
                text = "Label",
                style = TurninButtonStyle.Large,
                enabled = false,
                onClick = {},
            )
            TurninSolidButton(
                text = "Label",
                style = TurninButtonStyle.Medium,
                enabled = false,
                onClick = {},
            )
            TurninSolidButton(
                text = "Label",
                style = TurninButtonStyle.Small,
                enabled = false,
                onClick = {},
            )
            TurninSolidButton(
                text = "Label",
                style = TurninButtonStyle.Tiny,
                enabled = false,
                onClick = {},
            )
        }
    }
}

@Preview(widthDp = 500)
@Composable
private fun OutlinedButton_Default() {
    TurninAppTheme {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TurninOutlinedButton(
                text = "Label",
                style = TurninButtonStyle.Large,
                onClick = {},
            )
            TurninOutlinedButton(
                text = "Label",
                style = TurninButtonStyle.Medium,
                onClick = {},
            )
            TurninOutlinedButton(
                text = "Label",
                style = TurninButtonStyle.Small,
                onClick = {},
            )
            TurninOutlinedButton(
                text = "Label",
                style = TurninButtonStyle.Tiny,
                onClick = {},
            )
        }
    }
}

@Preview(widthDp = 500)
@Composable
private fun OutlinedButton_Icon() {
    TurninAppTheme {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TurninOutlinedButton(
                text = "Label",
                style = TurninButtonStyle.Large,
                icon = TurninIcons.Default.Normal.Arrow2Left,
                onClick = {},
            )
            TurninOutlinedButton(
                text = "Label",
                style = TurninButtonStyle.Medium,
                icon = TurninIcons.Default.Normal.Arrow2Left,
                onClick = {},
            )
            TurninOutlinedButton(
                text = "Label",
                style = TurninButtonStyle.Small,
                icon = TurninIcons.Default.Normal.Arrow2Left,
                onClick = {},
            )
            TurninOutlinedButton(
                text = "Label",
                style = TurninButtonStyle.Tiny,
                icon = TurninIcons.Default.Normal.Arrow2Left,
                onClick = {},
            )
        }
    }
}

@Preview(widthDp = 500)
@Composable
private fun OutlinedButton_Disabled() {
    TurninAppTheme {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TurninOutlinedButton(
                text = "Label",
                style = TurninButtonStyle.Large,
                enabled = false,
                onClick = {},
            )
            TurninOutlinedButton(
                text = "Label",
                style = TurninButtonStyle.Medium,
                enabled = false,
                onClick = {},
            )
            TurninOutlinedButton(
                text = "Label",
                style = TurninButtonStyle.Small,
                enabled = false,
                onClick = {},
            )
            TurninOutlinedButton(
                text = "Label",
                style = TurninButtonStyle.Tiny,
                enabled = false,
                onClick = {},
            )
        }
    }
}
