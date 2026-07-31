package com.turnin.core.designsystem.component.topbar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.turnin.core.designsystem.component.logo.TurninLogo
import com.turnin.core.designsystem.component.logo.TurninLogoType
import com.turnin.core.designsystem.util.click.ClickMode
import com.turnin.core.designsystem.util.click.clickableSingleWithoutRipple

/**
 * Turnin 로고가 포함된 TopBar
 *
 * @param modifier [Modifier]
 * @param leftSlot 왼쪽에 위치한 슬롯
 * @param rightSlot 오른쪽에 위치한 슬롯
 * @param onLogoClick 로고 클릭 시
 */
@Composable
fun TurninLogoTopBar(
    modifier: Modifier = Modifier,
    leftSlot: @Composable (RowScope.() -> Unit)? = null,
    rightSlot: @Composable (RowScope.() -> Unit)? = null,
    onLogoClick: () -> Unit = {},
) {
    SlotBasedCoreTopBar(
        modifier = modifier,
        leftSlot = leftSlot,
        rightSlot = rightSlot,
        centerSlot = {
            TurninLogo(
                logoType = TurninLogoType.Text,
                logoWidth = 92,
                modifier = Modifier.clickableSingleWithoutRipple(
                    clickMode = ClickMode.Throttle,
                    onClick = onLogoClick,
                ),
            )
        },
    )
}
