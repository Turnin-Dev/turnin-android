package com.peekr.presentation.discover

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.peekr.core.presentation.common.navigation.args.UserProfileArgs
import com.peekr.core.presentation.common.util.ObserveAsEvents
import com.peekr.presentation.discover.state.DiscoverContract
import com.peekr.presentation.discover.view.DiscoverScreen
import com.peekr.presentation.discover.viewmodel.DiscoverViewModel

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
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
        discoverContexts = discoverContexts,
        onUiEvent = viewModel::processEvent,
    )
}
