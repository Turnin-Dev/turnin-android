package com.peekr.presentation.profile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.peekr.core.presentation.common.navigation.Screens
import com.peekr.core.presentation.common.navigation.SubGraph
import com.peekr.presentation.profile.route.MyProfileRoute

fun NavGraphBuilder.myProfileNavigation(
    appNavController: NavHostController,
    bottomNavController: NavHostController,
) {
    navigation<SubGraph.BottomNav.Profile.Root>(
        startDestination = SubGraph.BottomNav.Profile.Me,
    ) {
        composable<SubGraph.BottomNav.Profile.Me> {
            MyProfileRoute(
                onSettingClick = {
                    // TODO: 임시 테스트 코드
                    bottomNavController.navigate(
                        SubGraph.BottomNav.Profile.User(1L),
                    )
                },
                onFriendsCountClick = { userId ->
                    bottomNavController.navigate(
                        Screens.FriendsList(userId),
                    )
                },
            )
        }
    }
}

private fun NavController.navigateToKeywordDetail(
    userKeywordId: Long,
    userId: Long,
    keyword: String,
) {
    navigate(
        Screens.KeywordDetail(
            userKeywordId = userKeywordId,
            userId = userId,
            keyword = keyword,
        ),
    )
}
