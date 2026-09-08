package com.turnin.presentation.profile

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.turnin.core.presentation.common.analytics.ScreenTrackers
import com.turnin.core.presentation.common.navigation.Route
import com.turnin.core.presentation.common.navigation.navigateToFriendsList
import com.turnin.core.presentation.common.navigation.navigateToKeywordDetail
import com.turnin.core.presentation.common.navigation.navigateToKeywordEdit
import com.turnin.core.presentation.common.navigation.navigateToSetting
import com.turnin.presentation.profile.route.MyProfileRoute

/**
 * 바텀 네비게이션, 개별 화면 두 곳에서 사용된다.
 */
inline fun <reified T : Route> NavGraphBuilder.myProfileNavigation(
    navController: NavHostController,
) {
    composable<T> { backStackEntry ->
        ScreenTrackers(backStackEntry.toRoute<T>().analyticsName)

        MyProfileRoute(
            onSettingClick = {
                navController.navigateToSetting()
            },
            onFriendsCountClick = { userId ->
                navController.navigateToFriendsList(userId)
            },
            onNavigateToKeywordAddScreen = {
                navController.navigateToKeywordEdit(null)
            },
            onNavigateToKeywordDetail = { userId, userKeywordId ->
                navController.navigateToKeywordDetail(userId, userKeywordId)
            },
        )
    }
}
