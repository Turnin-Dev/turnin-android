package com.peekr.designsystem.util

import android.graphics.BlurMaskFilter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Figma의 Drop Shadow를 구현하는 Modifier 확장 함수
 * Figma 상에서의 파라미터와 값을 같게 설정해주면 된다.
 */
fun Modifier.dropShadow(
    color: Color = Color.Black,
    borderRadius: Dp = 0.dp,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    blur: Dp = 0.dp,
    spread: Dp = 0.dp,
    modifier: Modifier = Modifier,
) = then(
    modifier.drawBehind {
        drawIntoCanvas { canvas ->
            val paint = Paint()
            val frameworkPaint = paint.asFrameworkPaint()
            val spreadPixel = spread.toPx()
            val leftPixel = (0f - spreadPixel) + offsetX.toPx()
            val topPixel = (0f - spreadPixel) + offsetY.toPx()
            val rightPixel = size.width + spreadPixel
            val bottomPixel = size.height + spreadPixel

            frameworkPaint.color = color.toArgb()

            if (blur != 0.dp) {
                frameworkPaint.maskFilter =
                    BlurMaskFilter(
                        blur.toPx(),
                        BlurMaskFilter.Blur.NORMAL,
                    )
            }

            canvas.drawRoundRect(
                left = leftPixel,
                top = topPixel,
                right = rightPixel,
                bottom = bottomPixel,
                radiusX = borderRadius.toPx(),
                radiusY = borderRadius.toPx(),
                paint = paint,
            )
        }
    },
)
