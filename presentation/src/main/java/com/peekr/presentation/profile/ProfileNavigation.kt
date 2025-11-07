package com.peekr.presentation.profile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navigation
import com.peekr.core.presentation.navigation.ProfileGraph
import com.peekr.core.presentation.navigation.Screens
import com.peekr.core.presentation.navigation.SubGraph
import com.peekr.presentation.keywordDetail.KeywordDetailRoute

fun NavGraphBuilder.profileNavigation(navController: NavHostController) {
    navigation<SubGraph.Profile>(startDestination = ProfileGraph.Main) {
        composable<ProfileGraph.Main> {
            ProfileRoute(
                onOpenKeywordDetailModal = { userKeywordId, userId, keyword ->
                    navController.navigateKeywordDetail(userKeywordId.value, userId.value, keyword)
                },
            )
        }

        dialog<Screens.KeywordDetail> { navBackStackEntry ->
            KeywordDetailRoute(
                onCancel = { navController.popBackStack() },
            )
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
