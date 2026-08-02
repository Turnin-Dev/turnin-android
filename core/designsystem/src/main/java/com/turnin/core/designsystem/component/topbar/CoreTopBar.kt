package com.turnin.core.designsystem.component.topbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import com.turnin.core.designsystem.R
import com.turnin.core.designsystem.component.button.TurninIconButton
import com.turnin.core.designsystem.component.icon.TurninIconSize
import com.turnin.core.designsystem.theme.TurninTheme
import com.turnin.core.designsystem.util.icon.Arrow1Left
import com.turnin.core.designsystem.util.icon.TurninIcons

object TurninTopBarTokens {
    val Height = 60.dp
}

/**
 * Slot 기반의 Core TopBar
 *
 * 좌, 우, 중앙에 슬롯이 존재하며, 각 슬롯에 원하는 컴포넌트를 넣어 사용할 수 있다.
 *
 * 만약 좌, 우 중 한 슬롯만 사용하게 되면 그에 맞게 정렬된다. 단, 중앙 슬롯은 무조건 중앙에서만 위치한다.
 *
 * (보편적이고 어느정도의 제약이 갖춰진 탑바를 사용하려면 [CoreTopBar]를 사용해야 한다.)
 *
 * @param modifier [Modifier]
 * @param leftSlot 왼쪽에 위치한 슬롯
 * @param rightSlot 오른쪽에 위치한 슬롯
 * @param centerSlot 중앙에 위치한 슬롯
 */
@Composable
internal fun SlotBasedCoreTopBar(
    modifier: Modifier = Modifier,
    leftSlot: @Composable (RowScope.() -> Unit)? = null,
    centerSlot: @Composable (BoxScope.() -> Unit)? = null,
    rightSlot: @Composable (RowScope.() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = TurninTopBarTokens.Height),
        contentAlignment = Alignment.Center,
    ) {
        // 좌우 영역
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = when {
                leftSlot != null && rightSlot != null -> Arrangement.SpaceBetween
                leftSlot != null -> Arrangement.Start
                rightSlot != null -> Arrangement.End
                else -> Arrangement.Center
            },
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
            ) {
                leftSlot?.invoke(this)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
            ) {
                rightSlot?.invoke(this)
            }
        }

        // 중앙 영역
        Box(contentAlignment = Alignment.Center) {
            centerSlot?.invoke(this)
        }
    }
}

/**
 * 모든 TopBar 의 기본이 되는 Core TopBar
 *
 * [CoreTopBar]의 모든 파라미터는 전부 `Nullable` 이며,
 * null 로 유지하면 해당 요소는 활성화되지 않는다. (시각적으로도 활성화되지 않음)
 *
 * (좀 더 자유롭고 제약이 느슨한 탑바를 사용하려면 [SlotBasedCoreTopBar]를 사용해야 한다.)
 *
 * **(참고: [logoSlot]을 사용할 땐 나머지 파라미터를 활성화하지 않는다.)**
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
            .heightIn(min = TurninTopBarTokens.Height),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LeftSection(
            modifier = Modifier.weight(1f),
            backPressedSlot = { onBackPressed?.let { BackPressedButton(onBackPressed) } },
            titleSlot = { title?.let { Title(title) } },
            logoSlot = logoSlot,
        )
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
    TurninIconButton(
        icon = TurninIcons.Default.Normal.Arrow1Left,
        contentDescription = stringResource(R.string.top_bar_btn_back_pressed),
        iconSize = TurninIconSize.Small,
        onClick = onClick,
    )
}

@Composable
private fun Title(text: String) {
    Text(
        text = text,
        style = TurninTheme.typography.title2,
        fontWeight = FontWeight.SemiBold,
        color = TurninTheme.colorScheme.textNormal,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}
