package com.peekr.presentation.profile

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.peekr.core.presentation.common.navigation.Screens
import com.peekr.core.presentation.common.navigation.SubGraph
import com.peekr.core.presentation.common.navigation.navigateToKeywordDetail
import com.peekr.core.presentation.common.navigation.navigateToKeywordEdit
import com.peekr.presentation.profile.route.MyProfileRoute

fun NavGraphBuilder.myProfileNavigation(
    appNavController: NavHostController,
    bottomNavController: NavHostController,
) {
    composable<SubGraph.BottomNav.Profile> {
        MyProfileRoute(
            onSettingClick = {
                // TODO: 임시 테스트 코드
                bottomNavController.navigate(
                    Screens.UserProfile(1L),
                )
            },
            onFriendsCountClick = { userId ->
                bottomNavController.navigate(
                    Screens.FriendsList(userId),
                )
            },
            onNavigateToKeywordAddScreen = {
                appNavController.navigateToKeywordEdit(null)
            },
            onNavigateToKeywordDetail = { userId, userKeywordId ->
                appNavController.navigateToKeywordDetail(userId, userKeywordId)
            },
        )
    }
}
