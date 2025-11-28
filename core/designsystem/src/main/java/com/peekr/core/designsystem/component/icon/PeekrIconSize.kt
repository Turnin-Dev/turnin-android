package com.peekr.core.designsystem.component.icon

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.component.button.PeekrIconButton

/**
 * [PeekrIconButton] 와 함께 사용하는 아이콘 사이즈
 *
 * @property size 아이콘 사이즈
 * @property touchTargetRadius 터치 타겟이 적용된 아이콘 사이즈의 반지름
 */
enum class PeekrIconSize(
    val size: Dp,
    val touchTargetRadius: Dp,
) {
    Large(32.dp, 26.dp),
    Medium(28.dp, 24.dp),
    Normal(24.dp, 22.dp),
    Small(20.dp, 20.dp),
    Tiny(16.dp, 18.dp),
}
