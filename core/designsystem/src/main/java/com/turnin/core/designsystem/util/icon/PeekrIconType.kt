package com.turnin.core.designsystem.util.icon

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource

/**
 * Peekr 아이콘 타입
 *
 * @param iconRes 아이콘 리소스
 */
@Immutable
class PeekrIconType(
    @DrawableRes private val iconRes: Int,
) {
    val imageVector: ImageVector
        @Composable
        get() = ImageVector.vectorResource(iconRes)
}
