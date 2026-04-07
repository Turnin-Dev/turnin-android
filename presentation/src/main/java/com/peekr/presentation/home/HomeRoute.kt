package com.peekr.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.presentation.common.navigation.args.UserProfileArgs
import com.peekr.presentation.home.view.HomeScreen
import com.peekr.presentation.home.viewmodel.HomeViewModel

@Composable
fun HomeRoute(
    onNavigateToKeywordDetail: (userId: Long, userKeywordId: Long) -> Unit,
    onNavigateToUserProfile: (UserProfileArgs) -> Unit,
    onNavigateToNotification: () -> Unit,
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val feeds = viewModel.feedsPagingData.collectAsLazyPagingItems()

    HomeScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(PeekrTheme.colorScheme.backgroundNormal),
        feeds = feeds,
        onFeedClick = { feed ->
            onNavigateToKeywordDetail(feed.userId, feed.userKeywordId)
        },
        onUserClick = { args ->
            onNavigateToUserProfile(args)
        },
        onNotificationClick = {
            onNavigateToNotification()
        },
    )
}
