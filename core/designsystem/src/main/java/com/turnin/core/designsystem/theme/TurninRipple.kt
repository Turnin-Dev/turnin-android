package com.turnin.core.designsystem.theme

import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RippleConfiguration
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
val TurninRipple = RippleConfiguration(
    color = Color.LightGray,
    rippleAlpha = RippleAlpha(0.1f, 0.1f, 0.1f, 0.1f),
)

@OptIn(ExperimentalMaterial3Api::class)
val TurninNoRipple = RippleConfiguration(
    color = Color.Unspecified,
    rippleAlpha = RippleAlpha(0f, 0f, 0f, 0f),
)
