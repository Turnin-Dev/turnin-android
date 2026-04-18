package com.peekr.core.presentation.common.navigation.bottom

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.peekr.core.designsystem.component.icon.PeekrIcon
import com.peekr.core.designsystem.component.icon.PeekrIconSize
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.click.clickableSingleWithoutRipple
import com.peekr.core.designsystem.util.icon.Home
import com.peekr.core.designsystem.util.icon.PeekrIconType
import com.peekr.core.designsystem.util.icon.PeekrIcons
import com.peekr.core.presentation.R
import com.peekr.core.presentation.common.navigation.SubGraph

/**
 * 바텀 네비게이션 바 공개용 토큰 값들
 */
object BottomNavigationBarTokens {
    val MinHeightDp = 64.dp
}

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
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = BottomNavigationBarTokens.MinHeightDp)
            .zIndex(1f),
        containerColor = PeekrTheme.colorScheme.backgroundNormal,
        windowInsets = WindowInsets(bottom = 0.dp),
    ) {
        Column(Modifier.align(Alignment.CenterVertically)) {
            // 상단 구분선
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = if (isSystemInDarkTheme()) Color(0xFF333333) else Color(0xFFD5D5D5),
                thickness = 0.5.dp,
            )

            // 아이템
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = BarHorizontalPadding),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                bottomNavItems.forEach { item ->
                    val checked by remember(currentDestination?.route) {
                        derivedStateOf {
                            currentDestination?.hierarchy?.any { dest ->
                                dest.hasRoute(item.route::class)
                            } == true
                        }
                    }

                    Item(
                        modifier = Modifier
                            .weight(1f)
                            .clickableSingleWithoutRipple {
                                navigateWithOption(
                                    navController = navController,
                                    currentRoute = item.route,
                                    currentDestination = currentDestination,
                                )
                            }
                            .padding(vertical = ItemVerticalSpacingDp),
                        icon = item.icon,
                        title = item.title,
                        checked = checked,
                    )
                }
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
) {
    val color = if (checked) {
        PeekrTheme.colorScheme.textStrong
    } else {
        PeekrTheme.colorScheme.interactionInactive
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(ItemIconTitleSpacingDp),
    ) {
        PeekrIcon(
            icon = icon,
            contentDescription = stringResource(title),
            iconSize = PeekrIconSize.Normal,
            tint = color,
        )
        Text(
            text = stringResource(title),
            style = PeekrTheme.typography.caption2,
            fontWeight = FontWeight.Medium,
            color = color,
        )
    }
}

/**
 * 조건에 따라 navigate를 수행한다.
 *
 * @param navController 네비게이션 컨트롤러
 * @param currentRoute 현재 라우트
 * @param currentDestination 현재 목적지
 */
private fun navigateWithOption(
    navController: NavHostController,
    currentRoute: SubGraph,
    currentDestination: NavDestination?,
) {
    val isCurrentTab = currentDestination?.hierarchy?.any { dest ->
        dest.hasRoute(currentRoute::class)
    } == true

    // 현재 선택된 탭이 현재 라우트와 같다면 선택된 탭까지 전부 스택에서 제거한다.
    if (isCurrentTab) {
        navController.popBackStack(
            route = currentRoute::class,
            inclusive = false,
        )
    } else {
        // 현재 선택된 탭과 다르다면 그냥 navigate 수행
        // 첫 번째 화면만 스택에 쌓이므로 뒤로가기 시 첫 번째 화면으로 이동한다.
        navController.navigate(currentRoute) {
            navController.graph.findStartDestination().route?.let {
                popUpTo(it) {
                    saveState = true
                }
            }

            launchSingleTop = true
            restoreState = true
        }
    }
}

private val ItemIconTitleSpacingDp = 4.dp
private val ItemVerticalSpacingDp = 8.dp
private val BarHorizontalPadding = 10.dp

// ------------------------------ Previews ------------------------------
@Preview
@Composable
private fun IconPreview() {
    PeekrAppTheme {
        Row(Modifier.width(150.dp)) {
            Item(
                modifier = Modifier
                    .weight(1f)
                    .clickable {},
                icon = PeekrIcons.Filled.Normal.Home,
                title = R.string.bottom_nav_item_home,
                checked = false,
            )
            Item(
                modifier = Modifier.weight(1f),
                icon = PeekrIcons.Filled.Normal.Home,
                title = R.string.bottom_nav_item_home,
                checked = true,
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun BottomNavigationBarPreview() {
    PeekrAppTheme {
        val navController = rememberNavController()
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = { BottomNavigationBar(Modifier.fillMaxWidth(), navController) },
        ) { innerPadding ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
        }
    }
}
