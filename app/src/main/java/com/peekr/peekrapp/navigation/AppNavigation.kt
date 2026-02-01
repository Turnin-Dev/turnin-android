package com.peekr.peekrapp.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.peekr.core.presentation.common.navigation.Screens
import com.peekr.core.presentation.common.navigation.SubGraph
import com.peekr.presentation.friend.friendsListScreen
import com.peekr.presentation.keywordDetail.keywordDetailNavigation
import com.peekr.presentation.keywordEdit.keywordEditNavigation
import com.peekr.presentation.login.loginNavigation
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
    val bottomNavController = rememberNavController()

    if (loggedIn != null) {
        NavHost(
            modifier = modifier,
            navController = appNavController,
            startDestination = if (loggedIn) {
                SubGraph.BottomNav.Root
            } else {
                SubGraph.Login.Root
            },
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
                    bottomNavController = bottomNavController,
                )
            }

            // 사용자 프로필 네비게이션
            userProfileNavigation(appNavController)

            // 친구 목록 네비게이션
            friendsListScreen(appNavController)

            // 키워드 상세화면 네비게이션
            keywordDetailNavigation(appNavController)

            // 키워드 편집 네비게이션
            keywordEditNavigation(appNavController)

            // 신고 네비게이션
            reportNavigation(appNavController)

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
