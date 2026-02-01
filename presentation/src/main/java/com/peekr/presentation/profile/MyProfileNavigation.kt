package com.peekr.presentation.profile

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.peekr.core.presentation.common.navigation.SubGraph
import com.peekr.core.presentation.common.navigation.navigateToFriendsList
import com.peekr.core.presentation.common.navigation.navigateToKeywordDetail
import com.peekr.core.presentation.common.navigation.navigateToKeywordEdit
import com.peekr.presentation.profile.route.MyProfileRoute

fun NavGraphBuilder.myProfileNavigation(appNavController: NavHostController) {
    composable<SubGraph.BottomNav.Profile> {
        MyProfileRoute(
            onSettingClick = {
            },
            onFriendsCountClick = { userId ->
                appNavController.navigateToFriendsList(userId)
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
