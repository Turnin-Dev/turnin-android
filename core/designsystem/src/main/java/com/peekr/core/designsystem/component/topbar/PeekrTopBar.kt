package com.peekr.core.designsystem.component.topbar

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Peekr TopBar
 *
 * 기본 Horizontal 패딩은 아이콘 버튼 활성화 여부에 따라 달라진다.
 *
 * @param modifier [Modifier]
 * @param title 탑바 타이틀
 * @param onBackPressed 탑바 뒤로가기 시
 * @param optionSlot 탑바 오른쪽 부분에 위치한 추가 슬롯
 *
 * @see CoreTopBar
 */
@Composable
fun PeekrTopBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    onBackPressed: (() -> Unit)? = null,
    optionSlot: @Composable (RowScope.() -> Unit)? = null,
) {
    CoreTopBar(
        modifier = modifier.padding(
            when {
                onBackPressed != null -> PaddingValuesWithTouchTarget
                optionSlot != null -> PaddingValuesWithTouchTarget
                else -> DefaultPaddingValues
            },
        ),
        onBackPressed = onBackPressed,
        title = title,
        optionSlot = optionSlot,
    )
}

// 탑바 패딩
private val DefaultPaddingValues = PaddingValues(20.dp)
private val PaddingValuesWithTouchTarget = PaddingValues(10.dp)
