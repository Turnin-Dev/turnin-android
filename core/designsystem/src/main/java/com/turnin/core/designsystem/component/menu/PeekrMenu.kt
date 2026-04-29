package com.turnin.core.designsystem.component.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.turnin.core.designsystem.R
import com.turnin.core.designsystem.component.modal.PeekrModalBottomSheet
import com.turnin.core.designsystem.theme.PeekrTheme

/**
 * PeekrMenu
 *
 * (보통 [PeekrModalBottomSheet]와 함께 사용한다.)
 *
 * [PeekrMenuItem]를 사용하여 [PeekrMenu]를 구성한다
 *
 * @param menuItems 메뉴 아이템 ([PeekrMenuItem] 사용]
 * @param modifier [Modifier]
 * @param onCancel 취소 클릭 시 (null이 아닐 때만 취소 항목 활성화)
 *
 * @see [PeekrModalBottomSheet]
 *
 * @sample PeekrMenuPreviews
 */
@Composable
fun PeekrMenu(
    menuItems: @Composable (ColumnScope.() -> Unit),
    modifier: Modifier = Modifier,
    onCancel: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
    ) {
        menuItems()
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            thickness = 0.5.dp,
            color = PeekrTheme.colorScheme.lineNormal,
        )
        onCancel?.let {
            PeekrMenuItem(
                menuItemType = PeekrMenuItemType.Positive,
                text = stringResource(R.string.menu_btn_cancel),
                onItemClick = onCancel,
            )
        }
    }
}
