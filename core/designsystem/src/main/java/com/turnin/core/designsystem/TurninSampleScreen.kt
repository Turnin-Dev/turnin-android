package com.turnin.core.designsystem

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.turnin.core.designsystem.theme.TurninAppTheme
import com.turnin.core.designsystem.theme.TurninTheme
import com.turnin.core.designsystem.util.TurninShadowType
import com.turnin.core.designsystem.util.turninShadow

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
private fun TurninSampleScreen() {
    TurninAppTheme {
        val isDarkMode = if (isSystemInDarkTheme()) {
            "Dark"
        } else {
            "Light"
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(TurninTheme.colorScheme.backgroundNormal)
                .padding(top = 80.dp),
            contentPadding = PaddingValues(ContentPadding),
            verticalArrangement = Arrangement.spacedBy(ContentPadding),
            horizontalAlignment = Alignment.Start,
        ) {
            stickyHeader { Header("Typography") }
            item { TypographySample() }
            stickyHeader { Header("Color Scheme ($isDarkMode)") }
            item { ColorSchemeSample() }
            stickyHeader { Header("Shape") }
            item { ShapeSample() }
            stickyHeader { Header("Shadow") }
            item { ShadowSample() }
            stickyHeader { Header("Etc") }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ShadowSample() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .turninShadow(
                type = TurninShadowType.Normal,
                shape = CircleShape,
            ).background(TurninTheme.colorScheme.backgroundNormal, CircleShape)
            .clip(CircleShape)
            .clickable(
                indication = ripple(),
                interactionSource = remember { MutableInteractionSource() },
            ) { },
        contentAlignment = Alignment.Center,
    ) {
        Text("Shadow", color = TurninTheme.colorScheme.textNormal)
    }
}

@Composable
private fun ShapeSample() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(ContentPadding),
    ) {
        ShapeElement("ExtraSmall", TurninTheme.shape.extraSmall)
        ShapeElement("Small", TurninTheme.shape.small)
        ShapeElement("Medium", TurninTheme.shape.medium)
        ShapeElement("Large", TurninTheme.shape.large)
        ShapeElement("ExtraLarge", TurninTheme.shape.extraLarge)
        ShapeElement("Modal", TurninTheme.shape.modal)
    }
}

@Composable
private fun ShapeElement(text: String, shapeSize: Int) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(shapeSize.dp))
            .size(100.dp)
            .background(TurninTheme.colorScheme.backgroundNormal)
            .border(1.dp, TurninTheme.colorScheme.textStrong, RoundedCornerShape(shapeSize.dp))
            .clickable(
                indication = ripple(),
                interactionSource = remember { MutableInteractionSource() },
            ) { },
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = TurninTheme.colorScheme.textNormal)
    }
}

@Composable
private fun ColorSchemeSample() {
    Column(verticalArrangement = Arrangement.spacedBy(ContentPadding)) {
        ColorSection(TurninTheme.colorScheme.primary)
        ColorSection(TurninTheme.colorScheme.accentYellow)
        ColorSection(TurninTheme.colorScheme.accentGreen)
        ColorSection(TurninTheme.colorScheme.accentPurple)
        ColorSection(TurninTheme.colorScheme.backgroundNormal, true)
        ColorSection(TurninTheme.colorScheme.backgroundAssist)
        ColorSection(TurninTheme.colorScheme.textStrong)
        ColorSection(TurninTheme.colorScheme.textNormal)
        ColorSection(TurninTheme.colorScheme.textAssist)
        ColorSection(TurninTheme.colorScheme.textAssist2)
        ColorSection(TurninTheme.colorScheme.textPlaceholder)
        ColorSection(TurninTheme.colorScheme.lineNormal)
        ColorSection(TurninTheme.colorScheme.lineDivider)
        ColorSection(TurninTheme.colorScheme.interactionClick)
        ColorSection(TurninTheme.colorScheme.interactionInactive)
        ColorSection(TurninTheme.colorScheme.interactionDisable)
        ColorSection(TurninTheme.colorScheme.statusPositive)
        ColorSection(TurninTheme.colorScheme.statusNegative)
        ColorSection(TurninTheme.colorScheme.staticWhite, true)
        ColorSection(TurninTheme.colorScheme.staticBlack)
        ColorSection(TurninTheme.colorScheme.componentEdge)
    }
}

@Composable
private fun ColorSection(color: Color, border: Boolean = false) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(color)
            .border(1.dp, if (border) Color.LightGray else Color.Transparent),
    )
}

@Composable
private fun TypographySample() {
    Column(verticalArrangement = Arrangement.spacedBy(ContentPadding)) {
        Text(
            text = "Title 1",
            style = TurninTheme.typography.title1,
            color = TurninTheme.colorScheme.textStrong,
        )
        Text(
            text = "Title 2",
            style = TurninTheme.typography.title2,
            color = TurninTheme.colorScheme.textStrong,
        )
        HorizontalDivider()
        Text(
            text = "Headline 1",
            style = TurninTheme.typography.headline2,
            color = TurninTheme.colorScheme.textStrong,
        )
        Text(
            text = "Headline 2",
            style = TurninTheme.typography.headline3,
            color = TurninTheme.colorScheme.textStrong,
        )
        Text(
            text = "Headline 3",
            style = TurninTheme.typography.headline4,
            color = TurninTheme.colorScheme.textStrong,
        )
        Text(
            text = "Headline 4",
            style = TurninTheme.typography.headline5,
            color = TurninTheme.colorScheme.textStrong,
        )
        HorizontalDivider()
        Text(
            text = "Body 1",
            style = TurninTheme.typography.body1,
            color = TurninTheme.colorScheme.textStrong,
        )
        Text(
            text = "Body 2",
            style = TurninTheme.typography.body2,
            color = TurninTheme.colorScheme.textStrong,
        )
        Text(
            text = "Body 3 Normal",
            style = TurninTheme.typography.body3,
            color = TurninTheme.colorScheme.textStrong,
        )
        Text(
            text = "Body 3 Many",
            style = TurninTheme.typography.body3,
            color = TurninTheme.colorScheme.textStrong,
        )
        Text(
            text = "Body 4",
            style = TurninTheme.typography.body4,
            color = TurninTheme.colorScheme.textStrong,
        )
        HorizontalDivider()
        Text(
            text = "Label 1",
            style = TurninTheme.typography.label1,
            color = TurninTheme.colorScheme.textStrong,
        )
        Text(
            text = "Label 2",
            style = TurninTheme.typography.label2,
            color = TurninTheme.colorScheme.textStrong,
        )
        HorizontalDivider()
        Text(
            text = "Caption 1",
            style = TurninTheme.typography.caption1,
            color = TurninTheme.colorScheme.textStrong,
        )
        Text(
            text = "Caption 2",
            style = TurninTheme.typography.caption2,
            color = TurninTheme.colorScheme.textStrong,
        )
        Text(
            text = "Caption 3",
            style = TurninTheme.typography.caption3,
            color = TurninTheme.colorScheme.textStrong,
        )
    }
}

@Composable
private fun Header(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(TurninTheme.colorScheme.textNormal)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = TurninTheme.typography.title1,
            color = TurninTheme.colorScheme.backgroundNormal,
        )
    }
}

private val ContentPadding = 16.dp
