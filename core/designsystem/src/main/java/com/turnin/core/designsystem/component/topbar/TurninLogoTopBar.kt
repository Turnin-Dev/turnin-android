package com.turnin.core.designsystem.component.topbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.turnin.core.designsystem.R
import com.turnin.core.designsystem.util.click.ClickMode
import com.turnin.core.designsystem.util.click.clickableSingleWithoutRipple

/**
 * Turnin 로고가 포함된 TopBar
 *
 * @param modifier [Modifier]
 * @param optionSlot 탑바 오른쪽 부분에 위치한 추가 슬롯
 * @param onLogoClick 로고 클릭 시
 */
@Composable
fun TurninLogoTopBar(
    modifier: Modifier = Modifier,
    optionSlot: @Composable (RowScope.() -> Unit)? = null,
    onLogoClick: () -> Unit = {},
) {
    CoreTopBar(
        modifier = modifier,
        optionSlot = optionSlot,
        logoSlot = {
            Image(
                modifier = Modifier
                    .size(LogoSize)
                    .clickableSingleWithoutRipple(
                        clickMode = ClickMode.Throttle,
                        onClick = onLogoClick,
                    ),
                imageVector = ImageVector.vectorResource(R.drawable.logo_text),
                contentDescription = stringResource(R.string.top_bar_logo),
                contentScale = ContentScale.Crop,
            )
        },
    )
}

// 로고 사이즈
private val LogoSize = DpSize(77.dp, 30.dp)
