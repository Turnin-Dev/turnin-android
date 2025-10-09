package com.peekr.core.designsystem.util

import android.graphics.BlurMaskFilter
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** PeekrShadow 타입 */
sealed class PeekrShadowType {
    data object Normal : PeekrShadowType()

    data class Custom(
        val shape: Shape = RectangleShape,
        val offsetX: Dp = 0.dp,
        val offsetY: Dp = 0.dp,
        val blur: Dp = 0.dp,
        val spread: Dp = 0.dp,
    ) : PeekrShadowType()
}

/** 그림자 방향 */
enum class DropShadowDirection {
    ALL,
    BOTTOM,
}

/**
 * PeekrShadow
 *
 * @param type 그림자 타입
 * @param shape 그림자 모양
 * @param direction 그림자 방향
 * @see PeekrShadowType
 * @see DropShadowDirection
 */
fun Modifier.peekrShadow(
    type: PeekrShadowType,
    shape: Shape = RectangleShape,
    direction: DropShadowDirection = DropShadowDirection.ALL,
): Modifier = composed {
    val token = type.getPeekrShadowToken(isSystemInDarkTheme())

    this.then(
        Modifier.dropShadow(
            shape = shape,
            direction = direction,
            color = token.color,
            offsetX = token.offsetX,
            offsetY = token.offsetY,
            blur = token.blur,
            spread = token.spread,
        ),
    )
}

// PeekrShadow 를 적용하기 위해 모아놓은 DropShadow 파라미터 모음
private data class PeekrShadowToken(
    val color: Color = Color.Black,
    val offsetX: Dp = 0.dp,
    val offsetY: Dp = 0.dp,
    val blur: Dp = 0.dp,
    val spread: Dp = 0.dp,
)

// PeekrShadowType 를 통해 PeekrShadowToken 를 가져온다.
private fun PeekrShadowType.getPeekrShadowToken(darkMode: Boolean): PeekrShadowToken = when (this) {
    PeekrShadowType.Normal -> {
        PeekrShadowToken(
            color = if (darkMode) Color.White.copy(0.25f) else Color.Black.copy(0.25f),
            blur = 3.dp,
        )
    }

    is PeekrShadowType.Custom -> {
        PeekrShadowToken(
            color = if (darkMode) Color.White.copy(0.25f) else Color.Black.copy(0.25f),
            offsetX = offsetX,
            offsetY = offsetY,
            blur = blur,
            spread = spread,
        )
    }
}

/**
 * Figma의 Drop Shadow를 구현하는 Modifier 확장 함수
 * Figma 상에서의 파라미터와 값을 같게 설정해주면 된다.
 */
private fun Modifier.dropShadow(
    shape: Shape,
    direction: DropShadowDirection = DropShadowDirection.ALL,
    color: Color = Color.Black,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    blur: Dp = 0.dp,
    spread: Dp = 0.dp,
    modifier: Modifier = Modifier,
) = then(
    modifier.drawBehind {
        val offsetXPx = offsetX.toPx()
        val offsetYPx = offsetY.toPx()
        val blurPx = blur.toPx()
        val spreadPx = spread.toPx()
        val shadowSize = Size(size.width + spreadPx, size.height + spreadPx)
        val shadowOutline = shape.createOutline(shadowSize, layoutDirection, this)

        val paint = Paint().apply {
            this.color = color
        }

        if (blurPx > 0) {
            paint.asFrameworkPaint().apply {
                maskFilter = BlurMaskFilter(blurPx, BlurMaskFilter.Blur.NORMAL)
            }
        }

        drawIntoCanvas { canvas ->
            canvas.save()

            // 방향에 따른 클리핑
            when (direction) {
                DropShadowDirection.BOTTOM -> {
                    canvas.clipRect(
                        left = 0f,
                        top = size.height,
                        right = size.width,
                        bottom = size.height + blurPx + spreadPx + offsetYPx,
                    )
                }

                DropShadowDirection.ALL -> {
                    // 클리핑 필요없음
                }
            }

            canvas.translate(offsetXPx, offsetYPx)
            canvas.drawOutline(shadowOutline, paint)
            canvas.restore()
        }
    },
)
