package com.peekr.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.presentation.home.view.HomeScreen
import com.peekr.presentation.home.viewmodel.HomeViewModel

@Composable
fun HomeRoute() {
    val viewModel: HomeViewModel = hiltViewModel()
    val feeds = viewModel.feedsPagingData.collectAsLazyPagingItems()

    HomeScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(PeekrTheme.colorScheme.backgroundNormal),
        feeds = feeds,
        onFeedClick = { feed -> },
        onNotificationClick = {},
    )
}
