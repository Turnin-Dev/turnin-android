package com.turnin.presentation.discover

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.turnin.core.designsystem.theme.TurninTheme
import com.turnin.core.presentation.common.navigation.args.UserProfileArgs
import com.turnin.core.presentation.common.util.ObserveAsEvents
import com.turnin.presentation.discover.state.DiscoverContract
import com.turnin.presentation.discover.view.DiscoverScreen
import com.turnin.presentation.discover.viewmodel.DiscoverViewModel

@Composable
fun DiscoverRoute(
    navigateToKeywordDetail: (userId: Long, userKeywordId: Long) -> Unit,
    navigateToUserProfile: (UserProfileArgs) -> Unit,
    navigateToMyProfile: () -> Unit,
) {
    val viewModel: DiscoverViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val discoverContexts = viewModel.discoverContexts.collectAsLazyPagingItems()

    ObserveAsEvents(viewModel.effect) {
        when (it) {
            is DiscoverContract.UiEffect.NavigateToKeywordDetail -> {
                navigateToKeywordDetail(it.userId, it.userKeywordId)
            }

            is DiscoverContract.UiEffect.NavigateToUserProfile -> {
                navigateToUserProfile(it.args)
            }

            DiscoverContract.UiEffect.NavigateToMyProfile -> {
                navigateToMyProfile()
            }
        }
    }

    DiscoverScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(TurninTheme.colorScheme.backgroundNormal),
        uiState = uiState,
        discoverContexts = discoverContexts,
        onUiEvent = viewModel::processEvent,
    )
}
