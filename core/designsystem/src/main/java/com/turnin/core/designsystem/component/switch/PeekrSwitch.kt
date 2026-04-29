package com.turnin.core.designsystem.component.switch

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.turnin.core.designsystem.theme.PeekrTheme
import com.turnin.core.designsystem.util.click.ClickMode
import com.turnin.core.designsystem.util.click.clickableSingle

/**
 * [PeekrSwitch]에서 사용하는 Switch Size
 *
 * @param width 스위치 버튼의 가로 길이
 * @param height 스위치 버튼의 세로 길이
 * @param padding 스위치 버튼과 컨테이너 사이의 패딩
 * @param containerWidth 스위치를 감싸고 있는 컨테이너 가로 길이
 * @param containerHeight 스위치를 감싸고 있는 컨테이너 세로 길이
 * @param iconPadding 아이콘과 스위치 버튼 사이의 패딩
 */
enum class PeekrSwitchSize(val width: Int, val height: Int, val padding: Double) {
    Small(24, 18, 1.0),
    Medium(32, 24, 1.3),
    Large(40, 30, 1.6),
    ;

    val containerWidth: Double = (width * 2) + (padding * 2)
    val containerHeight: Double = height + (padding * 2)
    val iconPadding: Double = padding + 2.0
}

/**
 * Peekr Switch
 *
 * @param checked 체크 여부
 * @param onCheckedChanged 체크 변경 시
 * @param size [PeekrSwitchSize]
 * @param modifier [Modifier]
 *
 * @see PeekrSwitchSize
 *
 * @sample PeekrSwitchPreview
 */
@Composable
fun PeekrSwitch(
    checked: Boolean,
    onCheckedChanged: (Boolean) -> Unit,
    size: PeekrSwitchSize,
    modifier: Modifier = Modifier,
) {
    val animatedOffsetX by animateDpAsState(
        targetValue = if (checked) size.width.dp else 0.dp,
        animationSpec = tween(
            durationMillis = 180,
            easing = FastOutSlowInEasing,
        ),
    )

    // ------------------------------ Switch Container ------------------------------
    Box(
        modifier = modifier
            .size(size.containerWidth.dp, size.containerHeight.dp)
            .clip(SwitchShape)
            .background(
                color = if (checked) {
                    PeekrTheme.colorScheme.primary
                } else {
                    PeekrTheme.colorScheme.interactionDisable
                },
                shape = SwitchShape,
            )
            .clickableSingle(
                clickMode = ClickMode.Throttle,
                onClick = { onCheckedChanged(!checked) },
            )
            .padding(size.padding.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        // ------------------------------ Switch Content ------------------------------
        Box(
            modifier = Modifier
                .graphicsLayer {
                    translationX = animatedOffsetX.toPx()
                }
                .clip(SwitchShape)
                .width(size.width.dp)
                .height(size.height.dp)
                .background(PeekrTheme.colorScheme.backgroundNormal, SwitchShape),
        )
    }
}

private val SwitchShape = RoundedCornerShape(100.dp)
