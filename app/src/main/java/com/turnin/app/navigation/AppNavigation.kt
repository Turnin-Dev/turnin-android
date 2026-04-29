package com.turnin.app.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.turnin.core.presentation.common.navigation.Screens
import com.turnin.core.presentation.common.navigation.SubGraph
import com.turnin.presentation.block.blockListScreen
import com.turnin.presentation.block.blockModalNavigation
import com.turnin.presentation.friend.friendsListScreen
import com.turnin.presentation.keywordDetail.keywordDetailNavigation
import com.turnin.presentation.keywordEdit.keywordEditNavigation
import com.turnin.presentation.login.loginNavigation
import com.turnin.presentation.notification.notificationScreen
import com.turnin.presentation.profile.myProfileNavigation
import com.turnin.presentation.profile.userProfileNavigation
import com.turnin.presentation.register.registerNavigation
import com.turnin.presentation.report.reportNavigation
import com.turnin.presentation.setting.settingNavigation

/**
 * Peekr의 앱 네비게이션
 */
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    appNavController: NavHostController,
    loggedIn: Boolean?,
    onCheckPermission: () -> Unit,
) {
    if (loggedIn != null) {
        NavHost(
            modifier = modifier,
            navController = appNavController,
            startDestination = if (loggedIn) {
                SubGraph.BottomNav.Root
            } else {
                SubGraph.Login.Root
            },
            enterTransition = _root_ide_package_.com.turnin.app.navigation.getEnterTransition(),
            exitTransition = _root_ide_package_.com.turnin.app.navigation.getExitTransition(Screens.KeywordEdit(null)),
            popEnterTransition = _root_ide_package_.com.turnin.app.navigation.getPopEnterTransition(Screens.KeywordEdit(null)),
            popExitTransition = _root_ide_package_.com.turnin.app.navigation.getPopExitTransition(),
        ) {
            // 로그인 네비게이션
            loginNavigation(
                navController = appNavController,
                navigateToMain = {
                    appNavController.navigate(SubGraph.BottomNav.Root) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )

            // 회원가입 네비게이션
            registerNavigation(
                navController = appNavController,
                navigateToMain = {
                    appNavController.navigate(SubGraph.BottomNav.Root) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )

            // 바텀 네비게이션
            composable<SubGraph.BottomNav.Root> {
                _root_ide_package_.com.turnin.app.navigation.BottomNavigation(
                    modifier = Modifier.fillMaxSize(),
                    appNavController = appNavController,
                    onCheckPermission = onCheckPermission,
                )
            }

            // 나의 프로필
            myProfileNavigation<Screens.MyProfile>(appNavController)

            // 사용자 프로필 네비게이션
            userProfileNavigation(appNavController)

            // 친구 목록 화면
            friendsListScreen(appNavController)

            // 키워드 상세화면 네비게이션
            keywordDetailNavigation(appNavController)

            // 키워드 편집 네비게이션
            keywordEditNavigation(appNavController)

            // 신고 네비게이션
            reportNavigation(appNavController)

            // 차단 모달 네비게이션
            blockModalNavigation(appNavController)

            // 차단 목록 네비게이션
            blockListScreen(appNavController)

            // 설정 네비게이션
            settingNavigation(appNavController)

            // 알림 화면
            notificationScreen(appNavController)
        }
    }
}

private fun getEnterTransition(
    vararg excludeScreen: Screens,
): AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
    {
        if (excludeScreen.any { initialState.destination.hasRoute(it::class) }) {
            EnterTransition.None
        } else {
            fadeIn(tween(250)) + slideIntoContainer(
                towards = SlideDirection.Start,
                animationSpec = tween(250),
                initialOffset = { it / 4 },
            )
        }
    }

private fun getExitTransition(
    vararg excludeScreen: Screens,
): AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    if (excludeScreen.any { targetState.destination.hasRoute(it::class) }) {
        ExitTransition.None
    } else {
        fadeOut(tween(250)) + slideOutOfContainer(
            towards = SlideDirection.Start,
            animationSpec = tween(250),
            targetOffset = { it / 4 },
        )
    }
}

private fun getPopEnterTransition(
    vararg excludeScreen: Screens,
): AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
    {
        if (excludeScreen.any { initialState.destination.hasRoute(it::class) }) {
            EnterTransition.None
        } else {
            fadeIn(tween(250)) + slideIntoContainer(
                towards = SlideDirection.End,
                animationSpec = tween(250),
                initialOffset = { it / 4 },
            )
        }
    }

private fun getPopExitTransition(
    vararg excludeScreen: Screens,
): AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    if (excludeScreen.any { targetState.destination.hasRoute(it::class) }) {
        ExitTransition.None
    } else {
        fadeOut(tween(250)) + slideOutOfContainer(
            towards = SlideDirection.End,
            animationSpec = tween(250),
            targetOffset = { it / 4 },
        )
    }
}
