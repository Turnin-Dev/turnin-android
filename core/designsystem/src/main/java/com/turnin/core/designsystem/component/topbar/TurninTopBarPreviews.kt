package com.turnin.core.designsystem.component.topbar

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.turnin.core.designsystem.R
import com.turnin.core.designsystem.component.button.TurninIconButton
import com.turnin.core.designsystem.component.icon.TurninIconSize
import com.turnin.core.designsystem.util.icon.Settings
import com.turnin.core.designsystem.util.icon.TurninIcons

@Preview(showBackground = true)
@Composable
private fun TurninLogoTopBarPreview() {
    Column {
        TurninLogoTopBar()
        HorizontalDivider()
        TurninLogoTopBar(
            optionSlot = {
                TurninIconButton(
                    icon = TurninIcons.Outlined.Normal.Settings,
                    contentDescription = stringResource(R.string.top_bar_btn_back_pressed),
                    iconSize = TurninIconSize.Small,
                    onClick = { },
                )
            },
        )
        HorizontalDivider()
        TurninLogoTopBar(
            optionSlot = {
                TurninIconButton(
                    icon = TurninIcons.Outlined.Normal.Settings,
                    contentDescription = stringResource(R.string.top_bar_btn_back_pressed),
                    iconSize = TurninIconSize.Small,
                    onClick = { },
                )
                TurninIconButton(
                    icon = TurninIcons.Outlined.Normal.Settings,
                    contentDescription = stringResource(R.string.top_bar_btn_back_pressed),
                    iconSize = TurninIconSize.Small,
                    onClick = { },
                )
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TurninTopBar_Normal() {
    Column {
        TurninTopBar(title = "Title Text")
        HorizontalDivider()
        TurninTopBar(
            title = "Title Text",
            optionSlot = {
                TurninIconButton(
                    icon = TurninIcons.Outlined.Normal.Settings,
                    contentDescription = stringResource(R.string.top_bar_btn_back_pressed),
                    iconSize = TurninIconSize.Small,
                    onClick = { },
                )
            },
        )
        HorizontalDivider()
        TurninTopBar(
            title = "Title Text",
            optionSlot = {
                TurninIconButton(
                    icon = TurninIcons.Outlined.Normal.Settings,
                    contentDescription = stringResource(R.string.top_bar_btn_back_pressed),
                    iconSize = TurninIconSize.Small,
                    onClick = { },
                )
                TurninIconButton(
                    icon = TurninIcons.Outlined.Normal.Settings,
                    contentDescription = stringResource(R.string.top_bar_btn_back_pressed),
                    iconSize = TurninIconSize.Small,
                    onClick = { },
                )
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TurninTopBar_BackPressed() {
    Column {
        TurninTopBar(onBackPressed = {})
        HorizontalDivider()
        TurninTopBar(
            onBackPressed = {},
            optionSlot = {
                TurninIconButton(
                    icon = TurninIcons.Outlined.Normal.Settings,
                    contentDescription = stringResource(R.string.top_bar_btn_back_pressed),
                    iconSize = TurninIconSize.Small,
                    onClick = { },
                )
            },
        )
        HorizontalDivider()
        TurninTopBar(
            onBackPressed = {},
            optionSlot = {
                TurninIconButton(
                    icon = TurninIcons.Outlined.Normal.Settings,
                    contentDescription = stringResource(R.string.top_bar_btn_back_pressed),
                    iconSize = TurninIconSize.Small,
                    onClick = { },
                )
                TurninIconButton(
                    icon = TurninIcons.Outlined.Normal.Settings,
                    contentDescription = stringResource(R.string.top_bar_btn_back_pressed),
                    iconSize = TurninIconSize.Small,
                    onClick = { },
                )
            },
        )
        HorizontalDivider()
    }
}

@Preview(showBackground = true)
@Composable
private fun TurninTopBar_BackPressed_Title() {
    Column {
        TurninTopBar(onBackPressed = {}, title = "Title Text")
        HorizontalDivider()
        TurninTopBar(
            onBackPressed = {},
            optionSlot = {
                TurninIconButton(
                    icon = TurninIcons.Outlined.Normal.Settings,
                    contentDescription = stringResource(R.string.top_bar_btn_back_pressed),
                    iconSize = TurninIconSize.Small,
                    onClick = { },
                )
            },
            title = "Title Text",
        )
        HorizontalDivider()
        TurninTopBar(
            onBackPressed = {},
            optionSlot = {
                TurninIconButton(
                    icon = TurninIcons.Outlined.Normal.Settings,
                    contentDescription = stringResource(R.string.top_bar_btn_back_pressed),
                    iconSize = TurninIconSize.Small,
                    onClick = { },
                )
                TurninIconButton(
                    icon = TurninIcons.Outlined.Normal.Settings,
                    contentDescription = stringResource(R.string.top_bar_btn_back_pressed),
                    iconSize = TurninIconSize.Small,
                    onClick = { },
                )
            },
            title = "Title Text",
        )
        HorizontalDivider()
    }
}
