package com.peekr.presentation.shared.bottom.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.peekr.designsystem.component.icon.PeekrIcon
import com.peekr.designsystem.component.icon.PeekrIconSize
import com.peekr.designsystem.theme.PeekrAppTheme
import com.peekr.designsystem.theme.PeekrTheme
import com.peekr.designsystem.util.click.clickableSingle
import com.peekr.designsystem.util.icon.Home
import com.peekr.designsystem.util.icon.PeekrIconType
import com.peekr.designsystem.util.icon.PeekrIcons
import com.peekr.presentation.R
import com.peekr.presentation.shared.SubGraph

/**
 * 바텀 네비게이션 바
 *
 * @param modifier [Modifier]
 * @param navController [NavHostController]
 */
@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentHierarchy = navBackStackEntry?.destination?.hierarchy

    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = BarHeightDp)
            .zIndex(1f),
        containerColor = PeekrTheme.colorScheme.backgroundNormal,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            bottomNavItems.forEach { item ->
                val checked by remember(currentRoute) {
                    derivedStateOf {
                        currentHierarchy?.any { it.hasRoute(item.route::class) } == true
                    }
                }
                Item(
                    icon = item.icon,
                    title = item.title,
                    checked = checked,
                    onClick = { onItemClickWithOptions(navController, item.route) },
                )
            }
        }
    }
}

/**
 * 바텀 네비게이션 아이템
 *
 * @param modifier [Modifier]
 * @param icon 아이템 아이콘
 * @param title 아이템 타이틀
 * @param checked 아이템 체크 여부
 */
@Composable
private fun Item(
    modifier: Modifier = Modifier,
    icon: PeekrIconType,
    @StringRes title: Int,
    checked: Boolean,
    onClick: () -> Unit,
) {
    val color = if (checked) {
        PeekrTheme.colorScheme.interactionInactive
    } else {
        PeekrTheme.colorScheme.textStrong
    }

    Column(
        modifier = modifier.clickableSingle(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(itemSpacingDp),
    ) {
        PeekrIcon(
            icon = icon,
            contentDescription = stringResource(title),
            iconSize = PeekrIconSize.Normal,
            tint = color,
        )
        Text(
            text = stringResource(title),
            style = PeekrTheme.typography.caption1,
            fontWeight = FontWeight.Medium,
            color = color,
        )
    }
}

/**
 * 선택한 화면으로 이동하게 해주는 함수
 * 1. popUpTo(it) { saveState = true }:
 * 첫 번째 화면만 스택에 쌓이게 하고 백버튼 클릭 시 첫 번째 화면으로 이동한다.
 * 2. launchSingleTop: true 일 때 화면 인스턴스가 하나만 만들어진다.
 * 3. restoreState: true 일 때 버튼을 재 클릭 했을 때 이전 상태가 남아있게 한다.
 */
private fun onItemClickWithOptions(
    navController: NavHostController,
    route: SubGraph,
) {
    navController.navigate(route) {
        navController.graph.startDestinationRoute?.let {
            // 첫번째 화면만 스택에 쌓이게 -> 백버튼 클릭 시 첫번째 화면으로 감
            if (route != BottomNavItem.Home.route) {
                popUpTo(it) { saveState = true }
            }
        }
        launchSingleTop = true
        restoreState = true
    }
}

private val BarHeightDp = 64.dp
private val itemSpacingDp = 6.dp

// ------------------------------ Previews ------------------------------
@Preview
@Composable
private fun IconPreview() {
    PeekrAppTheme {
        Row(Modifier.width(150.dp)) {
            Item(
                modifier = Modifier.weight(1f),
                icon = PeekrIcons.Filled.Home,
                title = R.string.bottom_nav_item_home,
                checked = false,
                onClick = {},
            )
            Item(
                modifier = Modifier.weight(1f),
                icon = PeekrIcons.Filled.Home,
                title = R.string.bottom_nav_item_home,
                checked = true,
                onClick = {},
            )
        }
    }
}
