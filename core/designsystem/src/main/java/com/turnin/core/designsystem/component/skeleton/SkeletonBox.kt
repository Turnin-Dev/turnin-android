package com.turnin.core.designsystem.component.skeleton

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.turnin.core.designsystem.theme.TurninAppTheme
import com.turnin.core.designsystem.theme.TurninTheme
import com.turnin.core.designsystem.util.shimmerEffect

/**
 * 스켈레톤 스크린에서 사용
 *
 * @param modifier [Modifier]
 * @param shape 요소의 모양
 */
@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(11.dp),
) {
    Box(
        modifier =
            modifier
                .clip(shape)
                .shimmerEffect()
                .background(TurninTheme.colorScheme.componentShimmer, shape),
    )
}

@Preview
@Composable
private fun SkeletonBoxPreview() {
    TurninAppTheme {
        SkeletonBox(Modifier.size(200.dp, 50.dp))
    }
}
