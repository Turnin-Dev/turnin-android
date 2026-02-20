package com.peekr.peekrapp.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.peekr.core.presentation.common.navigation.Screens
import com.peekr.core.presentation.common.navigation.SubGraph
import com.peekr.presentation.block.blockListScreen
import com.peekr.presentation.block.blockModalNavigation
import com.peekr.presentation.friend.friendsListScreen
import com.peekr.presentation.keywordDetail.keywordDetailNavigation
import com.peekr.presentation.keywordEdit.keywordEditNavigation
import com.peekr.presentation.login.loginNavigation
import com.peekr.presentation.profile.myProfileNavigation
import com.peekr.presentation.profile.userProfileNavigation
import com.peekr.presentation.register.registerNavigation
import com.peekr.presentation.report.reportNavigation

/**
 * Peekr의 앱 네비게이션
 */
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    appNavController: NavHostController,
    loggedIn: Boolean?,
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
            // TODO: 테스트용 트랜지션
            enterTransition = getEnterTransition(),
            exitTransition = getExitTransition(Screens.KeywordEdit(null)),
            popEnterTransition = getPopEnterTransition(Screens.KeywordEdit(null)),
            popExitTransition = getPopExitTransition(),
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
                BottomNavigation(
                    modifier = Modifier.fillMaxSize(),
                    appNavController = appNavController,
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

            // 임시 화면
            composable<Screens.TempMain> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Main Screen", fontSize = 50.sp)
                }
            }
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
