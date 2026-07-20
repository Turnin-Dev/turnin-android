package com.turnin.core.designsystem.component.dropDownMenu

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuBoxScope
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.turnin.core.designsystem.component.icon.TurninIcon
import com.turnin.core.designsystem.theme.TurninAppTheme
import com.turnin.core.designsystem.theme.TurninTheme
import com.turnin.core.designsystem.util.TurninShadowType
import com.turnin.core.designsystem.util.click.clickableSingle
import com.turnin.core.designsystem.util.icon.Arrow1Down
import com.turnin.core.designsystem.util.icon.Like
import com.turnin.core.designsystem.util.icon.Profile
import com.turnin.core.designsystem.util.icon.TurninIconType
import com.turnin.core.designsystem.util.icon.TurninIcons
import com.turnin.core.designsystem.util.turninShadow

/**
 * [TurninDropDownMenus] 에서 사용하는 드롭다운 메뉴 항목
 *
 * @property value 항목 값
 * @property icon 항목 아이콘
 *
 * @see TurninIconType
 */
data class TurninDropDownMenuItem(
    val value: String,
    val icon: TurninIconType,
)

/**
 * Turnin DropDownMenus
 *
 * @param modifier [Modifier]
 * @param expanded 드롭다운 메뉴 확장 여부
 * @param items 항목 목록
 * @param selectedItem 선택된 항목
 * @param onExpandedChange 드롭다운 메뉴 확장 여부 변경 시 수행할 콜백
 * @param onItemClick 항목 클릭 시 수행할 콜백
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TurninDropDownMenus(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    items: List<TurninDropDownMenuItem>,
    selectedItem: TurninDropDownMenuItem,
    onExpandedChange: (Boolean) -> Unit,
    onItemClick: (TurninDropDownMenuItem) -> Unit,
) {
    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { onExpandedChange(it) },
    ) {
        DropDownMenuTextField(
            modifier = Modifier
                .wrapContentWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            expanded = expanded,
            value = selectedItem.value,
        )

        DropDownMenus(
            expanded = expanded,
            selectedItem = selectedItem,
            items = items,
            onDismissRequest = { onExpandedChange(false) },
            onItemClick = { item ->
                onItemClick(item)
            },
        )
    }
}

/**
 * DropDownMenu를 사용하기 위한 TextField
 *
 * @param modifier [Modifier]
 * @param expanded 드롭다운 메뉴 확장 여부
 * @param value 표시될 항목 값
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropDownMenuTextField(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    value: String,
) {
    BasicTextField(
        modifier = modifier,
        value = value,
        onValueChange = {},
        readOnly = true,
        singleLine = true,
        enabled = false,
        decorationBox = { _ ->
            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .border(
                        width = 1.dp,
                        color = TurninTheme.colorScheme.textNormal,
                        shape = RoundedCornerShape(14.dp),
                    )
                    .padding(start = 10.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = value,
                    style = TurninTheme.typography.caption1,
                    fontWeight = FontWeight.Medium,
                    color = TurninTheme.colorScheme.textNormal,
                )
                Spacer(modifier = Modifier.width(6.dp))
                TurninIcon(
                    modifier = Modifier
                        .size(12.dp)
                        .rotate(if (expanded) 180f else 0f),
                    icon = TurninIcons.Default.Bold.Arrow1Down,
                    contentDescription = value,
                    tint = TurninTheme.colorScheme.textNormal,
                )
            }
        },
    )
}

/**
 * Turnin DropDownMenu
 *
 * 드롭다운 메뉴 확장 시 표시될 항목 목록
 *
 * @param modifier [Modifier]
 * @param expanded 드롭다운 메뉴 확장 여부
 * @param selectedItem 선택된 항목
 * @param items 항목 목록
 * @param onDismissRequest 드롭다운 메뉴가 사라질 때 수행할 콜백
 * @param onItemClick 항목 클릭 시 수행할 콜백
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExposedDropdownMenuBoxScope.DropDownMenus(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    selectedItem: TurninDropDownMenuItem,
    items: List<TurninDropDownMenuItem>,
    onDismissRequest: () -> Unit,
    onItemClick: (TurninDropDownMenuItem) -> Unit,
) {
    DropdownMenu(
        modifier = Modifier
            .turninShadow(
                type = TurninShadowType.Custom(
                    blur = 8.dp,
                    alpha = if (isSystemInDarkTheme()) 0.8f else 0.08f,
                    lightColor = Color.Black,
                    darkColor = Color.Black,
                ),
            )
            .background(TurninTheme.colorScheme.backgroundNormal)
            .padding(horizontal = 6.dp, vertical = 2.dp), // 기본 패딩 때문에 수직 패딩 약간 축소
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(18.dp),
        containerColor = TurninTheme.colorScheme.backgroundNormal,
    ) {
        items.forEachIndexed { index, item ->
            val isSelected = (item == selectedItem)

            if (index != 0) {
                Spacer(Modifier.height(6.dp))
            }

            Menu(
                item = item,
                isSelected = isSelected,
                onItemClick = { onItemClick(item) },
            )
        }
    }
}

/**
 * 각 메뉴 항목
 *
 * @param modifier [Modifier]
 * @param item 메뉴 항목
 * @param isSelected 선택 여부
 * @param onItemClick 항목 클릭 시 수행할 콜백
 */
@Composable
private fun Menu(
    modifier: Modifier = Modifier,
    item: TurninDropDownMenuItem,
    isSelected: Boolean,
    onItemClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .background(
                color = if (isSelected) TurninTheme.colorScheme.interactionClick else Color.Transparent,
                shape = MenuShape,
            )
            .clip(MenuShape)
            .clickableSingle(onClick = onItemClick)
            .padding(start = 14.dp, end = 32.dp, top = 12.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = item.icon.imageVector,
            contentDescription = item.value,
            tint = TurninTheme.colorScheme.textNormal,
        )
        Text(
            text = item.value,
            style = TurninTheme.typography.caption1,
            fontWeight = FontWeight.Medium,
            color = TurninTheme.colorScheme.textNormal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private val MenuShape = RoundedCornerShape(16.dp)

// ------------------------------ Previews ------------------------------

@Preview
@Composable
private fun MenuPreview() {
    TurninAppTheme {
        Menu(
            item = TurninDropDownMenuItem(
                value = "Option 1",
                icon = TurninIcons.Outlined.Normal.Profile,
            ),
            isSelected = true,
            onItemClick = {},
        )
    }
}

@Preview
@Composable
private fun TurninDropDownMenusPreview() {
    val items = listOf(
        TurninDropDownMenuItem("전체", TurninIcons.Outlined.Normal.Like),
        TurninDropDownMenuItem("친구", TurninIcons.Outlined.Normal.Profile),
    )
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf(items[0]) }

    TurninAppTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(TurninTheme.colorScheme.backgroundNormal)
                .padding(20.dp),
        ) {
            TurninDropDownMenus(
                expanded = expanded,
                items = items,
                selectedItem = selectedItem,
                onExpandedChange = { expanded = it },
                onItemClick = { item ->
                    selectedItem = item
                    expanded = false
                },
            )
        }
    }
}
