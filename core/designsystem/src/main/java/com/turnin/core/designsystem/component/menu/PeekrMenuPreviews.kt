package com.turnin.core.designsystem.component.menu

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.turnin.core.designsystem.theme.PeekrAppTheme
import com.turnin.core.designsystem.theme.PeekrTheme

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PeekrMenuItemPreviews() {
    PeekrAppTheme {
        Column(Modifier.background(PeekrTheme.colorScheme.backgroundNormal)) {
            PeekrMenuItem(
                menuItemType = PeekrMenuItemType.Positive,
                text = "Label",
                onItemClick = { },
            )
            PeekrMenuItem(
                menuItemType = PeekrMenuItemType.Negative,
                text = "Label",
                onItemClick = { },
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PeekrMenuPreviews() {
    PeekrAppTheme {
        Column(Modifier.background(PeekrTheme.colorScheme.backgroundNormal)) {
            PeekrMenu(
                modifier = Modifier.fillMaxWidth(),
                menuItems = {
                    PeekrMenuItem(
                        menuItemType = PeekrMenuItemType.Positive,
                        text = "Label 1",
                        onItemClick = { },
                    )
                    PeekrMenuItem(
                        menuItemType = PeekrMenuItemType.Negative,
                        text = "Label 2",
                        onItemClick = { },
                    )
                },
                onCancel = { },
            )
        }
    }
}
