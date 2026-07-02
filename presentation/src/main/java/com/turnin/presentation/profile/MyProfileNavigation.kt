package com.turnin.presentation.profile

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.turnin.core.presentation.common.navigation.navigateToFriendsList
import com.turnin.core.presentation.common.navigation.navigateToKeywordDetail
import com.turnin.core.presentation.common.navigation.navigateToKeywordEdit
import com.turnin.core.presentation.common.navigation.navigateToSetting
import com.turnin.presentation.profile.route.MyProfileRoute

inline fun <reified T : Any> NavGraphBuilder.myProfileNavigation(
    appNavController: NavHostController,
) {
    composable<T> {
        MyProfileRoute(
            onSettingClick = {
                appNavController.navigateToSetting()
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
