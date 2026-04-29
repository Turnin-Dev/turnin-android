package com.turnin.core.designsystem.component.menu

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.turnin.core.designsystem.theme.TurninAppTheme
import com.turnin.core.designsystem.theme.TurninTheme

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TurninMenuItemPreviews() {
    TurninAppTheme {
        Column(Modifier.background(TurninTheme.colorScheme.backgroundNormal)) {
            TurninMenuItem(
                menuItemType = TurninMenuItemType.Positive,
                text = "Label",
                onItemClick = { },
            )
            TurninMenuItem(
                menuItemType = TurninMenuItemType.Negative,
                text = "Label",
                onItemClick = { },
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TurninMenuPreviews() {
    TurninAppTheme {
        Column(Modifier.background(TurninTheme.colorScheme.backgroundNormal)) {
            TurninMenu(
                modifier = Modifier.fillMaxWidth(),
                menuItems = {
                    TurninMenuItem(
                        menuItemType = TurninMenuItemType.Positive,
                        text = "Label 1",
                        onItemClick = { },
                    )
                    TurninMenuItem(
                        menuItemType = TurninMenuItemType.Negative,
                        text = "Label 2",
                        onItemClick = { },
                    )
                },
                onCancel = { },
            )
        }
    }
}
