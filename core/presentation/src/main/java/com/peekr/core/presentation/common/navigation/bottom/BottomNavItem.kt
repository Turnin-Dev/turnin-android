package com.peekr.core.presentation.common.navigation.bottom

import androidx.annotation.StringRes
import com.peekr.core.designsystem.util.icon.Discover
import com.peekr.core.designsystem.util.icon.Home
import com.peekr.core.designsystem.util.icon.PeekrIconType
import com.peekr.core.designsystem.util.icon.PeekrIcons
import com.peekr.core.designsystem.util.icon.Profile
import com.peekr.core.presentation.R
import com.peekr.core.presentation.common.navigation.SubGraph

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
        route = SubGraph.BottomNav.Home,
        title = R.string.bottom_nav_item_home,
        icon = PeekrIcons.Filled.Normal.Home,
    )

    data object Discover : BottomNavItem(
        route = SubGraph.BottomNav.Discover,
        title = R.string.bottom_nav_item_discover,
        icon = PeekrIcons.Filled.Normal.Discover,
    )

    data object Profile : BottomNavItem(
        route = SubGraph.BottomNav.Profile.Root,
        title = R.string.bottom_nav_item_profile,
        icon = PeekrIcons.Filled.Normal.Profile,
    )
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Discover,
    BottomNavItem.Profile,
)
