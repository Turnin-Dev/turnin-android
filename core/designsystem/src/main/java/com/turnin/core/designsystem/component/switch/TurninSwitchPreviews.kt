package com.turnin.core.designsystem.component.switch

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.turnin.core.designsystem.theme.TurninAppTheme

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TurninSwitchPreview() {
    TurninAppTheme {
        val (checked, onCheckedChanged) = remember { mutableStateOf(false) }

        TurninSwitch(
            checked = checked,
            onCheckedChanged = onCheckedChanged,
            size = TurninSwitchSize.Medium,
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TurninSwitchListPreview() {
    TurninAppTheme {
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(TurninSwitchSize.entries) { switchSize ->
                val (checked, onCheckedChanged) = remember(switchSize) {
                    mutableStateOf(false)
                }
                TurninSwitch(
                    checked = checked,
                    onCheckedChanged = onCheckedChanged,
                    size = switchSize,
                )
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun TempPreview() {
    TurninAppTheme {
        Column {
            val (checked, onCheckedChanged) = remember { mutableStateOf(false) }
            TurninSwitch(
                checked = checked,
                onCheckedChanged = onCheckedChanged,
                size = TurninSwitchSize.Small,
            )

            val (checked2, onCheckedChanged2) = remember { mutableStateOf(true) }
            TurninSwitch(
                checked = checked2,
                onCheckedChanged = onCheckedChanged2,
                size = TurninSwitchSize.Small,
            )
        }
    }
}
