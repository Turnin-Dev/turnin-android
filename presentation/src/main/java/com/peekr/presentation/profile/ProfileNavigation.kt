package com.peekr.presentation.profile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.peekr.core.presentation.common.navigation.Screens
import com.peekr.core.presentation.common.navigation.SubGraph
import com.peekr.presentation.profile.route.MyProfileRoute

fun NavGraphBuilder.profileNavigation(
    appNavController: NavHostController,
    bottomNavController: NavHostController,
    test: () -> Unit,
) {
    navigation<SubGraph.BottomNav.Profile.Root>(
        startDestination = SubGraph.BottomNav.Profile.Me,
    ) {
        composable<SubGraph.BottomNav.Profile.Me> {
            MyProfileRoute(
                onOpenKeywordDetailModal = { userKeywordId, userId, keyword ->
                    appNavController.navigateKeywordDetail(userKeywordId.value, userId.value, keyword)
                },
                onSettingClick = {
                    test()
                },
            )
        }

        composable<SubGraph.BottomNav.Profile.User> {
            // UserProfileRoute
        }
    }
}

private fun NavController.navigateKeywordDetail(
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
