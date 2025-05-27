package com.peekr.designsystem.component.switch

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.peekr.designsystem.theme.PeekrAppTheme
import com.peekr.designsystem.theme.PeekrTheme
import com.peekr.designsystem.util.icon.PeekrIcons
import com.peekr.designsystem.util.icon.People
import com.peekr.designsystem.util.icon.Profile

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PeekrSwitchPreview() {
    PeekrAppTheme {
        val (checked, onCheckedChanged) = remember { mutableStateOf(false) }

        PeekrSwitch(
            checked = checked,
            onCheckedChanged = onCheckedChanged,
            size = PeekrSwitchSize.Medium,
            uncheckedIcon = {
                Icon(
                    modifier = Modifier.padding(PeekrSwitchSize.Medium.iconPadding.dp),
                    imageVector = PeekrIcons.Outlined.Normal.Profile.imageVector,
                    contentDescription = null,
                    tint = PeekrTheme.colorScheme.backgroundNormal,
                )
            },
            checkedIcon = {
                Icon(
                    modifier = Modifier.padding(PeekrSwitchSize.Medium.iconPadding.dp),
                    imageVector = PeekrIcons.Outlined.Normal.People.imageVector,
                    contentDescription = null,
                    tint = PeekrTheme.colorScheme.backgroundNormal,
                )
            },
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PeekrSwitchListPreview() {
    PeekrAppTheme {
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(PeekrSwitchSize.entries) { switchSize ->
                val (checked, onCheckedChanged) = remember(switchSize) {
                    mutableStateOf(false)
                }
                PeekrSwitch(
                    checked = checked,
                    onCheckedChanged = onCheckedChanged,
                    size = switchSize,
                    uncheckedIcon = {
                        Icon(
                            modifier = Modifier.padding(switchSize.iconPadding.dp),
                            imageVector = PeekrIcons.Outlined.Normal.Profile.imageVector,
                            contentDescription = null,
                            tint = PeekrTheme.colorScheme.backgroundNormal,
                        )
                    },
                    checkedIcon = {
                        Icon(
                            modifier = Modifier.padding(switchSize.iconPadding.dp),
                            imageVector = PeekrIcons.Outlined.Normal.People.imageVector,
                            contentDescription = null,
                            tint = PeekrTheme.colorScheme.backgroundNormal,
                        )
                    },
                )
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun TempPreview() {
    PeekrAppTheme {
        Column {
            val (checked, onCheckedChanged) = remember { mutableStateOf(false) }
            PeekrSwitch(
                checked = checked,
                onCheckedChanged = onCheckedChanged,
                size = PeekrSwitchSize.Small,
                uncheckedIcon = {
                    Icon(
                        modifier = Modifier.padding(PeekrSwitchSize.Small.iconPadding.dp),
                        imageVector = PeekrIcons.Outlined.Normal.Profile.imageVector,
                        contentDescription = null,
                        tint = PeekrTheme.colorScheme.backgroundNormal,
                    )
                },
                checkedIcon = {
                    Icon(
                        modifier = Modifier.padding(1.5.dp),
                        imageVector = PeekrIcons.Outlined.Normal.People.imageVector,
                        contentDescription = null,
                        tint = PeekrTheme.colorScheme.backgroundNormal,
                    )
                },
            )

            val (checked2, onCheckedChanged2) = remember { mutableStateOf(true) }
            PeekrSwitch(
                checked = checked2,
                onCheckedChanged = onCheckedChanged2,
                size = PeekrSwitchSize.Small,
                uncheckedIcon = {
                    Icon(
                        modifier = Modifier.padding(PeekrSwitchSize.Small.iconPadding.dp),
                        imageVector = PeekrIcons.Outlined.Normal.Profile.imageVector,
                        contentDescription = null,
                        tint = PeekrTheme.colorScheme.backgroundNormal,
                    )
                },
                checkedIcon = {
                    Icon(
                        modifier = Modifier.padding(1.5.dp),
                        imageVector = PeekrIcons.Outlined.Normal.People.imageVector,
                        contentDescription = null,
                        tint = PeekrTheme.colorScheme.backgroundNormal,
                    )
                },
            )
        }
    }
}
