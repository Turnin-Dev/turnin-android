package com.peekr.core.designsystem.component.icon

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.component.button.PeekrIconButton

/** [PeekrIconButton] 와 함께 사용하는 아이콘 사이즈 */
enum class PeekrIconSize(
    val size: Dp,
    val touchTargetRadius: Dp,
) {
    Large(32.dp, 30.dp),
    Medium(28.dp, 26.dp),
    Normal(24.dp, 22.dp),
    Small(20.dp, 18.dp),
    Tiny(16.dp, 14.dp),
}
