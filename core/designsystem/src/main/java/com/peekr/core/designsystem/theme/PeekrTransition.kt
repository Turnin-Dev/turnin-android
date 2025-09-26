package com.peekr.core.designsystem.theme

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

data class PeekrTransition(
    val fadeIn: EnterTransition = fadeIn(),
    val fadeOut: ExitTransition = fadeOut(),
)
