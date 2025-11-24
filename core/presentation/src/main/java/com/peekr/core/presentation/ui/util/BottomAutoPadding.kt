package com.peekr.core.presentation.ui.util

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.union
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.max
import com.peekr.core.designsystem.util.token.ScreenTokens

/**
 * 제스처 바와 키보드 패딩이 계산된 하단 패딩을 반환한다.
 *
 * 키보드가 활성화 되었을 때는 `키보드 패딩`을 존중하고 키보드가 비활성화 되었을 때는 [defaultPadding]패딩을 존중한다.
 */
@Composable
fun bottomAutoPadding(defaultPadding: Dp = ScreenTokens.BottomButtonPadding): Dp {
    val insets = WindowInsets.navigationBars.union(WindowInsets.ime)
    val insetBottom: Dp = insets.asPaddingValues().calculateBottomPadding()
    return max(defaultPadding, insetBottom)
}
