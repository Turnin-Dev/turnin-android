package com.peekr.designsystem.theme

import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RippleConfiguration
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
val PeekrRipple = RippleConfiguration(
    color = Color.LightGray,
    rippleAlpha = RippleAlpha(0.2f, 0.2f, 0.2f, 0.2f),
)

@OptIn(ExperimentalMaterial3Api::class)
val PeekrNoRipple = RippleConfiguration(
    color = Color.Unspecified,
    rippleAlpha = RippleAlpha(0f, 0f, 0f, 0f),
)
