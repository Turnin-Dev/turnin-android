package com.turnin.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.compose.collectAsLazyPagingItems
import com.turnin.core.designsystem.theme.TurninTheme
import com.turnin.core.presentation.common.navigation.args.UserProfileArgs
import com.turnin.presentation.home.view.HomeScreen
import com.turnin.presentation.home.viewmodel.HomeViewModel

@Composable
fun HomeRoute(
    onNavigateToKeywordDetail: (userId: Long, userKeywordId: Long) -> Unit,
    onNavigateToUserProfile: (UserProfileArgs) -> Unit,
    onNavigateToNotification: () -> Unit,
) {
    val viewModel: HomeViewModel = hiltViewModel()
    // 화면 전환 시 재구독으로 인한 LoadState.Refresh 트리거를 방지하기 위해
    // 구독 컨텍스트를 viewModelScope에 고정
    val feeds = viewModel.feedsPagingData.collectAsLazyPagingItems(viewModel.viewModelScope.coroutineContext)

    HomeScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(TurninTheme.colorScheme.backgroundNormal),
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
