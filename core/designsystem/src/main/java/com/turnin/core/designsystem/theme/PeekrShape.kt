package com.turnin.core.designsystem.theme

import androidx.compose.runtime.Immutable

@Immutable
data class PeekrShape(
    val extraSmall: Int = 4,
    val small: Int = 6,
    val medium: Int = 8,
    val large: Int = 10,
    val extraLarge: Int = 12,
    val modal: Int = 25,
)
