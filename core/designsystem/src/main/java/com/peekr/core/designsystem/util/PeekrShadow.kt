package com.peekr.core.designsystem.util

import androidx.annotation.FloatRange
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

/** PeekrShadow 타입 */
sealed class PeekrShadowType {
    data object Normal : PeekrShadowType()

    data class Custom(
        val shape: Shape = RectangleShape,
        val blur: Dp = 0.dp,
        val lightColor: Color = Color.Black,
        val darkColor: Color = Color.White,
        val spread: Dp = 0.dp,
        val offset: DpOffset = DpOffset(0.dp, 0.dp),
        @FloatRange(from = 0.0, to = 1.0) val alpha: Float = 1f,
    ) : PeekrShadowType()
}

/**
 * PeekrShadow
 *
 * @param type 그림자 타입
 * @param shape 그림자 모양
 * @see PeekrShadowType
 */
fun Modifier.peekrShadow(
    type: PeekrShadowType,
    shape: Shape = RectangleShape,
): Modifier = composed {
    val isDarkMode = isSystemInDarkTheme()
    val color = if (isDarkMode) Color.White else Color.Black

    when (type) {
        PeekrShadowType.Normal -> {
            this.dropShadow(
                shape = shape,
                shadow = Shadow(
                    radius = 3.dp,
                    color = color,
                    spread = 0.dp,
                    offset = DpOffset(0.dp, 0.dp),
                    alpha = 0.1f,
                ),
            )
        }

        is PeekrShadowType.Custom -> {
            this.dropShadow(
                shape = shape,
                shadow = Shadow(
                    radius = type.blur,
                    color = if (isDarkMode) type.darkColor else type.lightColor,
                    spread = type.spread,
                    offset = type.offset,
                    alpha = type.alpha,
                ),
            )
        }
    }
}
