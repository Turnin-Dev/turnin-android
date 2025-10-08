package com.peekr.core.designsystem.component.skeleton

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme

/**
 * 스켈레톤 스크린에서 사용
 *
 * @param size 요소의 DP 단위 사이즈
 * @param shape 요소의 모양
 */
@Composable
fun SkeletonBox(
    size: DpSize,
    shape: Shape = RoundedCornerShape(11.dp),
) {
    Box(
        modifier =
            Modifier
                .clip(shape)
                .size(size)
                .background(PeekrTheme.colorScheme.componentShimmer, shape),
    )
}

@Preview
@Composable
private fun SkeletonBoxPreview() {
    PeekrAppTheme {
        SkeletonBox(DpSize(200.dp, 50.dp))
    }
}
