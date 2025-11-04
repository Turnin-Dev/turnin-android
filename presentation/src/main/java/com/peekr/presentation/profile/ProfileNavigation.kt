package com.peekr.presentation.profile

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navigation
import com.peekr.core.presentation.navigation.ProfileGraph
import com.peekr.core.presentation.navigation.Screens
import com.peekr.core.presentation.navigation.SubGraph
import com.peekr.presentation.keywordDetail.KeywordDetailRoute
import com.peekr.presentation.keywordDetail.viewmodel.KeywordDetailViewModel

fun NavGraphBuilder.profileNavigation(navController: NavHostController) {
    navigation<SubGraph.Profile>(startDestination = ProfileGraph.Main) {
        composable<ProfileGraph.Main> {
            ProfileRoute(
                onOpenKeywordDetailModal = { userKeywordId, keyword, description ->
                    navController.navigate(Screens.KeywordDetail)
                },
            )
        }

        dialog<Screens.KeywordDetail> {
            val viewModel: KeywordDetailViewModel = hiltViewModel()

            KeywordDetailRoute(
                viewModel = viewModel,
                onCancel = { navController.popBackStack() },
            )
        }
    }
}
