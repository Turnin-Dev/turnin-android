package com.peekr.presentation.common.bottom.navigation

import androidx.annotation.StringRes
import com.peekr.designsystem.util.icon.Discover
import com.peekr.designsystem.util.icon.Home
import com.peekr.designsystem.util.icon.PeekrIconType
import com.peekr.designsystem.util.icon.PeekrIcons
import com.peekr.designsystem.util.icon.Profile
import com.peekr.presentation.R
import com.peekr.presentation.common.SubGraph

/**
 * 메인 화면에서 사용하는 바텀 네비게이션 아이템
 *
 * @param route 아이템 라우트
 * @param title 아이템 제목 리소스 (아이콘 설명(contentDescription)에서도 사용된다)
 * @param icon 아이템 아이콘 리소스
 */
sealed class BottomNavItem(
    val route: SubGraph,
    @StringRes val title: Int,
    val icon: PeekrIconType,
) {
    data object Home : BottomNavItem(
        route = SubGraph.Home,
        title = R.string.bottom_nav_item_home,
        icon = PeekrIcons.Filled.Home,
    )

    data object Discover : BottomNavItem(
        route = SubGraph.Discover,
        title = R.string.bottom_nav_item_discover,
        icon = PeekrIcons.Filled.Discover,
    )

    data object Profile : BottomNavItem(
        route = SubGraph.Profile,
        title = R.string.bottom_nav_item_profile,
        icon = PeekrIcons.Filled.Profile,
    )
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Discover,
    BottomNavItem.Profile,
)
