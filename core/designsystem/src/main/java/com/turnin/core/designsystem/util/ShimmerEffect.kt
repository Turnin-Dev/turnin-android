package com.turnin.core.designsystem.util

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private const val ANIMATION_SPEED = 700

/** Shimmer Effect */
fun Modifier.shimmerEffect() =
    composed {
        val infiniteTransition = rememberInfiniteTransition(label = "shimmer_effect")
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(ANIMATION_SPEED),
                    repeatMode = RepeatMode.Reverse,
                ),
            label = "",
        )

        alpha(alpha)
    }

@Preview
@Composable
private fun ShimmerEffectPreview() {
    Box(
        modifier =
            Modifier
                .clip(CircleShape)
                .size(150.dp)
                .shimmerEffect()
                .background(Color.LightGray),
    )
}
