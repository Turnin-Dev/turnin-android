package com.peekr.core.presentation.keyword.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.theme.PeekrTheme

/**
 * 키워드 엣지(간선) 컴포넌트
 *
 * 중심을 기준으로 타겟 오프셋([targetX], [targetY])까지 이어진다.
 *
 * @param targetX 타겟 오프셋 X
 * @param targetY 타겟 오프셋 Y
 * @param stroke 엣지(간선) 두께
 * @param color 엣지(간선) 색상
 */
@Composable
fun KeywordEdge(
    targetX: Float,
    targetY: Float,
    stroke: Float = 0.5f,
    color: Color = PeekrTheme.colorScheme.componentEdge,
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2

        drawLine(
            color = color,
            start = Offset(centerX, centerY),
            end = Offset(targetX, targetY),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.dp.toPx(), 2.dp.toPx()), 0f),
        )
    }
}

@Preview
@Composable
private fun KeywordEdgePreview() {
    val density = LocalDensity.current

    Box(Modifier.size(100.dp), Alignment.Center) {
        KeywordEdge(
            targetX = with(density) { 0.dp.toPx() },
            targetY = with(density) { 50.dp.toPx() },
        )
    }
}
