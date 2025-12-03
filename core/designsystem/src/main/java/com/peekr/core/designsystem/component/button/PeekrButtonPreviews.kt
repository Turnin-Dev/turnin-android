package com.peekr.core.designsystem.component.button

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
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.util.icon.Arrow2Left
import com.peekr.core.designsystem.util.icon.PeekrIcons
import kotlinx.coroutines.delay

@Preview(widthDp = 500)
@Composable
private fun SolidButton_Default() {
    PeekrAppTheme {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PeekrSolidButton(
                text = "Label",
                style = PeekrButtonStyle.Large,
                onClick = {},
            )
            PeekrSolidButton(
                text = "Label",
                style = PeekrButtonStyle.Medium,
                onClick = {},
            )
            PeekrSolidButton(
                text = "Label",
                style = PeekrButtonStyle.Small,
                onClick = {},
            )
            PeekrSolidButton(
                text = "Label",
                style = PeekrButtonStyle.Tiny,
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

    PeekrAppTheme {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PeekrSolidButton(
                text = "Label",
                style = PeekrButtonStyle.Large,
                loading = clicked,
                onClick = { clicked = true },
            )
            PeekrSolidButton(
                text = "Label",
                style = PeekrButtonStyle.Medium,
                loading = true,
                onClick = {},
            )
            PeekrSolidButton(
                text = "Label",
                style = PeekrButtonStyle.Small,
                loading = true,
                onClick = {},
            )
            PeekrSolidButton(
                text = "Label",
                style = PeekrButtonStyle.Tiny,
                loading = true,
                onClick = {},
            )
        }
    }
}

@Preview(widthDp = 500)
@Composable
private fun SolidButton_Icon() {
    PeekrAppTheme {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PeekrSolidButton(
                text = "Label",
                style = PeekrButtonStyle.Large,
                icon = PeekrIcons.Default.Normal.Arrow2Left,
                onClick = {},
            )
            PeekrSolidButton(
                text = "Label",
                style = PeekrButtonStyle.Medium,
                icon = PeekrIcons.Default.Normal.Arrow2Left,
                onClick = {},
            )
            PeekrSolidButton(
                text = "Label",
                style = PeekrButtonStyle.Small,
                icon = PeekrIcons.Default.Normal.Arrow2Left,
                onClick = {},
            )
            PeekrSolidButton(
                text = "Label",
                style = PeekrButtonStyle.Tiny,
                icon = PeekrIcons.Default.Normal.Arrow2Left,
                onClick = {},
            )
        }
    }
}

@Preview(widthDp = 500)
@Composable
private fun SolidButton_Disabled() {
    PeekrAppTheme {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PeekrSolidButton(
                text = "Label",
                style = PeekrButtonStyle.Large,
                enabled = false,
                onClick = {},
            )
            PeekrSolidButton(
                text = "Label",
                style = PeekrButtonStyle.Medium,
                enabled = false,
                onClick = {},
            )
            PeekrSolidButton(
                text = "Label",
                style = PeekrButtonStyle.Small,
                enabled = false,
                onClick = {},
            )
            PeekrSolidButton(
                text = "Label",
                style = PeekrButtonStyle.Tiny,
                enabled = false,
                onClick = {},
            )
        }
    }
}

@Preview(widthDp = 500)
@Composable
private fun OutlinedButton_Default() {
    PeekrAppTheme {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PeekrOutlinedButton(
                text = "Label",
                style = PeekrButtonStyle.Large,
                onClick = {},
            )
            PeekrOutlinedButton(
                text = "Label",
                style = PeekrButtonStyle.Medium,
                onClick = {},
            )
            PeekrOutlinedButton(
                text = "Label",
                style = PeekrButtonStyle.Small,
                onClick = {},
            )
            PeekrOutlinedButton(
                text = "Label",
                style = PeekrButtonStyle.Tiny,
                onClick = {},
            )
        }
    }
}

@Preview(widthDp = 500)
@Composable
private fun OutlinedButton_Icon() {
    PeekrAppTheme {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PeekrOutlinedButton(
                text = "Label",
                style = PeekrButtonStyle.Large,
                icon = PeekrIcons.Default.Normal.Arrow2Left,
                onClick = {},
            )
            PeekrOutlinedButton(
                text = "Label",
                style = PeekrButtonStyle.Medium,
                icon = PeekrIcons.Default.Normal.Arrow2Left,
                onClick = {},
            )
            PeekrOutlinedButton(
                text = "Label",
                style = PeekrButtonStyle.Small,
                icon = PeekrIcons.Default.Normal.Arrow2Left,
                onClick = {},
            )
            PeekrOutlinedButton(
                text = "Label",
                style = PeekrButtonStyle.Tiny,
                icon = PeekrIcons.Default.Normal.Arrow2Left,
                onClick = {},
            )
        }
    }
}

@Preview(widthDp = 500)
@Composable
private fun OutlinedButton_Disabled() {
    PeekrAppTheme {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PeekrOutlinedButton(
                text = "Label",
                style = PeekrButtonStyle.Large,
                enabled = false,
                onClick = {},
            )
            PeekrOutlinedButton(
                text = "Label",
                style = PeekrButtonStyle.Medium,
                enabled = false,
                onClick = {},
            )
            PeekrOutlinedButton(
                text = "Label",
                style = PeekrButtonStyle.Small,
                enabled = false,
                onClick = {},
            )
            PeekrOutlinedButton(
                text = "Label",
                style = PeekrButtonStyle.Tiny,
                enabled = false,
                onClick = {},
            )
        }
    }
}
