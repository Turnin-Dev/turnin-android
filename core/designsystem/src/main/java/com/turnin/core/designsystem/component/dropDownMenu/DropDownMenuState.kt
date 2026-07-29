package com.turnin.core.designsystem.component.dropDownMenu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/**
 * 드롭다운 메뉴 상태 클래스
 *
 * @param initialItems 드롭다운 메뉴 항목 리스트
 * @param initialSelectedIndex 선택된 항목의 인덱스, 기본 값은 0
 */
@Stable
class DropDownMenuState(
    initialItems: List<TurninDropDownMenuItem>,
    initialSelectedIndex: Int = 0,
) {
    /** 드롭다운 메뉴 확장 여부 */
    var expanded by mutableStateOf(false)
        private set

    /** 드롭다운 메뉴 항목 리스트 */
    var items by mutableStateOf(initialItems)
        private set

    /** 선택된 드롭다운 메뉴 항목 */
    var selectedIndex by mutableIntStateOf(initialSelectedIndex)
        private set

    /**
     * 확장 여부를 변경한다.
     *
     * @param isExpanded 확장 여부
     */
    fun changeExpanded(isExpanded: Boolean) {
        expanded = isExpanded
    }

    /**
     * 드롭다운 메뉴를 닫는다.
     */
    fun dismiss() {
        expanded = false
    }

    /**
     * 선택한 항목으로 변경하고 드롭다운 메뉴를 닫는다.
     *
     * @param index 선택할 항목의 인덱스
     */
    fun select(index: Int) {
        selectedIndex = index
        expanded = false
    }

    /**
     * 선택한 항목으로 변경하고 드롭다운 메뉴를 닫는다.
     *
     * @param item 선택할 항목
     */
    fun select(item: TurninDropDownMenuItem) {
        val findItemIndex = items.indexOf(item)
        selectedIndex = findItemIndex
        expanded = false
    }

    companion object {
        internal fun stateSaver(items: List<TurninDropDownMenuItem>): Saver<DropDownMenuState, *> =
            Saver(
                save = { listOf(it.expanded, it.selectedIndex) },
                restore = {
                    val (expanded, selectedIndex) = it as List<*>

                    DropDownMenuState(
                        initialItems = items,
                        initialSelectedIndex = selectedIndex as Int,
                    ).apply {
                        changeExpanded(expanded as Boolean)
                    }
                },
            )
    }
}

/**
 * 드롭다운 메뉴 상태
 *
 * @param items 드롭다운 메뉴 항목 리스트
 * @param initialSelectedIndex 초기 선택된 항목의 인덱스, 기본 값은 0
 *
 * @return [DropDownMenuState]
 */
@Composable
fun rememberDropDownMenuState(
    items: List<TurninDropDownMenuItem>,
    initialSelectedIndex: Int = 0,
): DropDownMenuState = rememberSaveable(saver = DropDownMenuState.stateSaver(items)) {
    DropDownMenuState(items, initialSelectedIndex)
}
