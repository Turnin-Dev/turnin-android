package com.peekr.designsystem

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
import com.peekr.designsystem.theme.PeekrAppTheme
import com.peekr.designsystem.theme.PeekrTheme
import com.peekr.designsystem.util.PeekrShadowType
import com.peekr.designsystem.util.peekrShadow

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
private fun PeekrSampleScreen() {
    PeekrAppTheme {
        val isDarkMode = if (isSystemInDarkTheme()) {
            "Dark"
        } else {
            "Light"
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(PeekrTheme.colorScheme.backgroundNormal)
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
            .peekrShadow(
                type = PeekrShadowType.Normal,
                shape = CircleShape,
            ).background(PeekrTheme.colorScheme.backgroundNormal, CircleShape)
            .clip(CircleShape)
            .clickable(
                indication = ripple(),
                interactionSource = remember { MutableInteractionSource() },
            ) { },
        contentAlignment = Alignment.Center,
    ) {
        Text("Shadow", color = PeekrTheme.colorScheme.textNormal)
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
        ShapeElement("ExtraSmall", PeekrTheme.shape.extraSmall)
        ShapeElement("Small", PeekrTheme.shape.small)
        ShapeElement("Medium", PeekrTheme.shape.medium)
        ShapeElement("Large", PeekrTheme.shape.large)
        ShapeElement("ExtraLarge", PeekrTheme.shape.extraLarge)
        ShapeElement("Modal", PeekrTheme.shape.modal)
    }
}

@Composable
private fun ShapeElement(text: String, shapeSize: Int) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(shapeSize.dp))
            .size(100.dp)
            .background(PeekrTheme.colorScheme.backgroundNormal)
            .border(1.dp, PeekrTheme.colorScheme.textStrong, RoundedCornerShape(shapeSize.dp))
            .clickable(
                indication = ripple(),
                interactionSource = remember { MutableInteractionSource() },
            ) { },
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = PeekrTheme.colorScheme.textNormal)
    }
}

@Composable
private fun ColorSchemeSample() {
    Column(verticalArrangement = Arrangement.spacedBy(ContentPadding)) {
        ColorSection(PeekrTheme.colorScheme.primary)
        ColorSection(PeekrTheme.colorScheme.accentYellow)
        ColorSection(PeekrTheme.colorScheme.accentGreen)
        ColorSection(PeekrTheme.colorScheme.accentPurple)
        ColorSection(PeekrTheme.colorScheme.backgroundNormal, true)
        ColorSection(PeekrTheme.colorScheme.backgroundAssist)
        ColorSection(PeekrTheme.colorScheme.textStrong)
        ColorSection(PeekrTheme.colorScheme.textNormal)
        ColorSection(PeekrTheme.colorScheme.textAssist)
        ColorSection(PeekrTheme.colorScheme.textAssist2)
        ColorSection(PeekrTheme.colorScheme.textPlaceholder)
        ColorSection(PeekrTheme.colorScheme.lineNormal)
        ColorSection(PeekrTheme.colorScheme.lineDivider)
        ColorSection(PeekrTheme.colorScheme.interactionClick)
        ColorSection(PeekrTheme.colorScheme.interactionInactive)
        ColorSection(PeekrTheme.colorScheme.interactionDisable)
        ColorSection(PeekrTheme.colorScheme.statusPositive)
        ColorSection(PeekrTheme.colorScheme.statusNegative)
        ColorSection(PeekrTheme.colorScheme.staticWhite, true)
        ColorSection(PeekrTheme.colorScheme.staticBlack)
        ColorSection(PeekrTheme.colorScheme.componentEdge)
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
            style = PeekrTheme.typography.title1,
            color = PeekrTheme.colorScheme.textStrong,
        )
        Text(
            text = "Title 2",
            style = PeekrTheme.typography.title2,
            color = PeekrTheme.colorScheme.textStrong,
        )
        HorizontalDivider()
        Text(
            text = "Headline 1",
            style = PeekrTheme.typography.headline1,
            color = PeekrTheme.colorScheme.textStrong,
        )
        Text(
            text = "Headline 2",
            style = PeekrTheme.typography.headline2,
            color = PeekrTheme.colorScheme.textStrong,
        )
        Text(
            text = "Headline 3",
            style = PeekrTheme.typography.headline3,
            color = PeekrTheme.colorScheme.textStrong,
        )
        Text(
            text = "Headline 4",
            style = PeekrTheme.typography.headline4,
            color = PeekrTheme.colorScheme.textStrong,
        )
        HorizontalDivider()
        Text(
            text = "Body 1",
            style = PeekrTheme.typography.body1,
            color = PeekrTheme.colorScheme.textStrong,
        )
        Text(
            text = "Body 2",
            style = PeekrTheme.typography.body2,
            color = PeekrTheme.colorScheme.textStrong,
        )
        Text(
            text = "Body 3 Normal",
            style = PeekrTheme.typography.body3Normal,
            color = PeekrTheme.colorScheme.textStrong,
        )
        Text(
            text = "Body 3 Many",
            style = PeekrTheme.typography.body3Many,
            color = PeekrTheme.colorScheme.textStrong,
        )
        Text(
            text = "Body 4",
            style = PeekrTheme.typography.body4,
            color = PeekrTheme.colorScheme.textStrong,
        )
        HorizontalDivider()
        Text(
            text = "Label 1",
            style = PeekrTheme.typography.label1,
            color = PeekrTheme.colorScheme.textStrong,
        )
        Text(
            text = "Label 2",
            style = PeekrTheme.typography.label2,
            color = PeekrTheme.colorScheme.textStrong,
        )
        HorizontalDivider()
        Text(
            text = "Caption 1",
            style = PeekrTheme.typography.caption1,
            color = PeekrTheme.colorScheme.textStrong,
        )
    }
}

@Composable
private fun Header(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(PeekrTheme.colorScheme.textNormal)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = PeekrTheme.typography.title1,
            color = PeekrTheme.colorScheme.backgroundNormal,
        )
    }
}

private val ContentPadding = 16.dp
