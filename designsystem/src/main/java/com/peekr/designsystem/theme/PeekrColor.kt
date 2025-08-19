package com.peekr.designsystem.theme

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

// @Stable: For DarkMode
@Stable
data class PeekrColor(
    val primary: Color,
    val accentYellow: Color,
    val accentGreen: Color,
    val accentPurple: Color,
    val backgroundNormal: Color,
    val backgroundAssist: Color,
    val textStrong: Color,
    val textNormal: Color,
    val textAssist: Color,
    val textAssist2: Color,
    val textPlaceholder: Color,
    val lineNormal: Color,
    val lineDivider: Color,
    val interactionInactive: Color,
    val interactionDisable: Color,
    val interactionClick: Color,
    val statusPositive: Color,
    val statusNegative: Color,
    val staticWhite: Color = Color(0xFFFFFFFF),
    val staticBlack: Color = Color(0xFF1C1C1C),
    val componentEdge: Color,
)

val peekrLightColor = PeekrColor(
    primary = Color(0xFFFF6451),
    accentYellow = Color(0xFFF5B301),
    accentGreen = Color(0xFFC1E1C1),
    accentPurple = Color(0xFFC8B8DB),
    backgroundNormal = Color(0xFFFFFFFF),
    backgroundAssist = Color(0xFFFCF8EB),
    textStrong = Color(0xFF000000),
    textNormal = Color(0xFF2B2B2B),
    textAssist = Color(0xFF53555C),
    textAssist2 = Color(0xFFB7B3B3),
    textPlaceholder = Color(0xFFDCDCDC),
    lineNormal = Color(0xFFD9D9D9),
    lineDivider = Color(0xFFF1F1F1),
    interactionInactive = Color(0xFF97999D),
    interactionDisable = Color(0xFFEEEEEE),
    interactionClick = Color(0xFFEAEAEA),
    statusPositive = Color(0xFF30EC44),
    statusNegative = Color(0xFFF74040),
    componentEdge = Color(0xFFBDBDBD),
)

val peekrDarkColor = PeekrColor(
    primary = Color(0xFFFF6451),
    accentYellow = Color(0xFFF5B301),
    accentGreen = Color(0xFFA4D4A4),
    accentPurple = Color(0xFFD9CBEA),
    backgroundNormal = Color(0xFF1A1A1A),
    backgroundAssist = Color(0xFF2B2B2B),
    textStrong = Color(0xFFFFFFFF),
    textNormal = Color(0xFFFFFFFF),
    textAssist = Color(0xFFA0A0A5),
    textAssist2 = Color(0xFF7C7C7F),
    textPlaceholder = Color(0xFFDCDCDC),
    lineNormal = Color(0xFF3A3A3A),
    lineDivider = Color(0xFF292828),
    interactionInactive = Color(0xFF97999D),
    interactionDisable = Color(0xFFD0D0D0),
    interactionClick = Color(0xFFEAEAEA),
    statusPositive = Color(0xFF30EC44),
    statusNegative = Color(0xFFF74040),
    componentEdge = Color(0xFFBDBDBD),
)
