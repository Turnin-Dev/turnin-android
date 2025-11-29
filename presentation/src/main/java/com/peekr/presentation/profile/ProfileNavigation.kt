package com.peekr.presentation.profile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.peekr.core.presentation.common.navigation.ProfileGraph
import com.peekr.core.presentation.common.navigation.Screens
import com.peekr.core.presentation.common.navigation.SubGraph

fun NavGraphBuilder.profileNavigation(
    appNavController: NavHostController,
    bottomNavController: NavHostController,
    test: () -> Unit,
) {
    navigation<SubGraph.Profile>(startDestination = ProfileGraph.Me) {
        composable<ProfileGraph.Me> {
            MyProfileRoute(
                onOpenKeywordDetailModal = { userKeywordId, userId, keyword ->
                    appNavController.navigateKeywordDetail(userKeywordId.value, userId.value, keyword)
                },
                onSettingClick = {
                    test()
                },
            )
        }

        composable<ProfileGraph.User> {
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
