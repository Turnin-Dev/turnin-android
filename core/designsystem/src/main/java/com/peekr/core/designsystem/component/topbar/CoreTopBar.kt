package com.peekr.core.designsystem.component.topbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.R
import com.peekr.core.designsystem.component.button.PeekrIconButton
import com.peekr.core.designsystem.component.icon.PeekrIconSize
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.icon.Arrow1Left
import com.peekr.core.designsystem.util.icon.PeekrIcons

/**
 * 모든 TopBar 의 기본이 되는 Core TopBar
 *
 * [CoreTopBar]의 모든 파라미터는 전부 `Nullable` 이며,
 * null 로 유지하면 해당 요소는 활성화되지 않는다. (시각적으로도 활성화되지 않음)
 *
 * **(단, [logoSlot]을 사용할 땐 나머지 파라미터를 활성화하지 않는다.)**
 *
 * @param modifier [Modifier]
 * @param title 탑바 타이틀
 * @param onBackPressed 탑바 뒤로가기 시
 * @param optionSlot 탑바 오른쪽 부분에 위치한 추가 슬롯
 * @param logoSlot 로고 슬롯
 */
@Composable
internal fun CoreTopBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    onBackPressed: (() -> Unit)? = null,
    optionSlot: @Composable (RowScope.() -> Unit)? = null,
    logoSlot: @Composable (RowScope.() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = TopBarHeight),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LeftSection(
            modifier = Modifier.wrapContentWidth(),
            backPressedSlot = { onBackPressed?.let { BackPressedButton(onBackPressed) } },
            titleSlot = { title?.let { Title(title) } },
            logoSlot = logoSlot,
        )
        Spacer(Modifier.weight(1f))
        RightSection(
            modifier = Modifier.wrapContentWidth(),
            optionSlot = optionSlot,
        )
    }
}

/**
 * [CoreTopBar]의 왼쪽 섹션
 *
 * @param modifier [Modifier]
 * @param backPressedSlot 가장 왼쪽에 위치하는 뒤로 가기 버튼 슬롯
 * @param titleSlot 타이틀이 위치하는 슬롯
 * @param logoSlot 로고가 위치하는 슬롯
 */
@Composable
private fun LeftSection(
    modifier: Modifier = Modifier,
    backPressedSlot: @Composable ((RowScope.() -> Unit))? = null,
    titleSlot: @Composable ((RowScope.() -> Unit))? = null,
    logoSlot: @Composable (RowScope.() -> Unit)? = null,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        backPressedSlot?.let { backPressedSlot() }
        titleSlot?.let { titleSlot() }
        logoSlot?.let { logoSlot() }
    }
}

/**
 * [CoreTopBar]의 오른쪽 섹션
 *
 * @param modifier [Modifier]
 * @param optionSlot 추가 슬롯
 */
@Composable
private fun RightSection(
    modifier: Modifier = Modifier,
    optionSlot: @Composable (RowScope.() -> Unit)? = null,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End,
    ) {
        optionSlot?.let { optionSlot() }
    }
}

@Composable
private fun BackPressedButton(onClick: () -> Unit) {
    PeekrIconButton(
        icon = PeekrIcons.Default.Normal.Arrow1Left,
        contentDescription = stringResource(R.string.top_bar_btn_back_pressed),
        iconSize = PeekrIconSize.Small,
        onClick = onClick,
    )
}

@Composable
private fun Title(text: String) {
    Text(
        text = text,
        style = PeekrTheme.typography.title2,
        fontWeight = FontWeight.SemiBold,
        color = PeekrTheme.colorScheme.textNormal,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

// TopBar 높이
private val TopBarHeight = 60.dp
