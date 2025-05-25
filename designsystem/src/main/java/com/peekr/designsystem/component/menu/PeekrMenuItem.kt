package com.peekr.designsystem.component.menu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.peekr.designsystem.theme.PeekrTheme
import com.peekr.designsystem.util.click.ClickMode
import com.peekr.designsystem.util.click.clickableSingle

/**
 * [PeekrMenuItem] 타입
 *
 * @param Positive 일반 항목
 * @param Negative 경고/강조 항목
 */
enum class PeekrMenuItemType {
    Positive,
    Negative,
}

/**
 * [PeekrMenu]와 함께 사용
 *
 * @param menuItemType 메뉴 아이템 타입 ([PeekrMenuItemType])
 * @param text 메뉴 아이템 이름
 * @param modifier [Modifier]
 * @param onItemClick 메뉴 아이템 클릭 시
 */
@Composable
fun PeekrMenuItem(
    menuItemType: PeekrMenuItemType,
    text: String,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clickableSingle(
                clickMode = ClickMode.Throttle,
                onClick = onItemClick,
            ).padding(vertical = 10.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = PeekrTheme.typography.body2,
            fontWeight = FontWeight.Medium,
            color = when (menuItemType) {
                PeekrMenuItemType.Positive -> PeekrTheme.colorScheme.textNormal
                PeekrMenuItemType.Negative -> PeekrTheme.colorScheme.statusNegative
            },
        )
    }
}
