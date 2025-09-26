package com.peekr.core.designsystem.component.topbar

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.peekr.core.designsystem.R
import com.peekr.core.designsystem.component.button.PeekrIconButton
import com.peekr.core.designsystem.component.icon.PeekrIconSize
import com.peekr.core.designsystem.util.icon.PeekrIcons
import com.peekr.core.designsystem.util.icon.Settings

@Preview(showBackground = true)
@Composable
private fun PeekrLogoTopBarPreview() {
    Column {
        PeekrLogoTopBar()
        HorizontalDivider()
        PeekrLogoTopBar(
            optionSlot = {
                PeekrIconButton(
                    icon = PeekrIcons.Outlined.Normal.Settings,
                    contentDescription = stringResource(R.string.top_bar_btn_back_pressed),
                    iconSize = PeekrIconSize.Small,
                    onClick = { },
                )
            },
        )
        HorizontalDivider()
        PeekrLogoTopBar(
            optionSlot = {
                PeekrIconButton(
                    icon = PeekrIcons.Outlined.Normal.Settings,
                    contentDescription = stringResource(R.string.top_bar_btn_back_pressed),
                    iconSize = PeekrIconSize.Small,
                    onClick = { },
                )
                PeekrIconButton(
                    icon = PeekrIcons.Outlined.Normal.Settings,
                    contentDescription = stringResource(R.string.top_bar_btn_back_pressed),
                    iconSize = PeekrIconSize.Small,
                    onClick = { },
                )
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PeekrTopBar_Normal() {
    Column {
        PeekrTopBar(title = "Title Text")
        HorizontalDivider()
        PeekrTopBar(
            title = "Title Text",
            optionSlot = {
                PeekrIconButton(
                    icon = PeekrIcons.Outlined.Normal.Settings,
                    contentDescription = stringResource(R.string.top_bar_btn_back_pressed),
                    iconSize = PeekrIconSize.Small,
                    onClick = { },
                )
            },
        )
        HorizontalDivider()
        PeekrTopBar(
            title = "Title Text",
            optionSlot = {
                PeekrIconButton(
                    icon = PeekrIcons.Outlined.Normal.Settings,
                    contentDescription = stringResource(R.string.top_bar_btn_back_pressed),
                    iconSize = PeekrIconSize.Small,
                    onClick = { },
                )
                PeekrIconButton(
                    icon = PeekrIcons.Outlined.Normal.Settings,
                    contentDescription = stringResource(R.string.top_bar_btn_back_pressed),
                    iconSize = PeekrIconSize.Small,
                    onClick = { },
                )
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PeekrTopBar_BackPressed() {
    Column {
        PeekrTopBar(onBackPressed = {})
        HorizontalDivider()
        PeekrTopBar(
            onBackPressed = {},
            optionSlot = {
                PeekrIconButton(
                    icon = PeekrIcons.Outlined.Normal.Settings,
                    contentDescription = stringResource(R.string.top_bar_btn_back_pressed),
                    iconSize = PeekrIconSize.Small,
                    onClick = { },
                )
            },
        )
        HorizontalDivider()
        PeekrTopBar(
            onBackPressed = {},
            optionSlot = {
                PeekrIconButton(
                    icon = PeekrIcons.Outlined.Normal.Settings,
                    contentDescription = stringResource(R.string.top_bar_btn_back_pressed),
                    iconSize = PeekrIconSize.Small,
                    onClick = { },
                )
                PeekrIconButton(
                    icon = PeekrIcons.Outlined.Normal.Settings,
                    contentDescription = stringResource(R.string.top_bar_btn_back_pressed),
                    iconSize = PeekrIconSize.Small,
                    onClick = { },
                )
            },
        )
        HorizontalDivider()
    }
}

@Preview(showBackground = true)
@Composable
private fun PeekrTopBar_BackPressed_Title() {
    Column {
        PeekrTopBar(onBackPressed = {}, title = "Title Text")
        HorizontalDivider()
        PeekrTopBar(
            onBackPressed = {},
            optionSlot = {
                PeekrIconButton(
                    icon = PeekrIcons.Outlined.Normal.Settings,
                    contentDescription = stringResource(R.string.top_bar_btn_back_pressed),
                    iconSize = PeekrIconSize.Small,
                    onClick = { },
                )
            },
            title = "Title Text",
        )
        HorizontalDivider()
        PeekrTopBar(
            onBackPressed = {},
            optionSlot = {
                PeekrIconButton(
                    icon = PeekrIcons.Outlined.Normal.Settings,
                    contentDescription = stringResource(R.string.top_bar_btn_back_pressed),
                    iconSize = PeekrIconSize.Small,
                    onClick = { },
                )
                PeekrIconButton(
                    icon = PeekrIcons.Outlined.Normal.Settings,
                    contentDescription = stringResource(R.string.top_bar_btn_back_pressed),
                    iconSize = PeekrIconSize.Small,
                    onClick = { },
                )
            },
            title = "Title Text",
        )
        HorizontalDivider()
    }
}
