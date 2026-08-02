package com.turnin.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.turnin.core.designsystem.component.dropDownMenu.TurninDropDownMenuItem
import com.turnin.core.designsystem.component.dropDownMenu.rememberDropDownMenuState
import com.turnin.core.designsystem.theme.TurninTheme
import com.turnin.core.designsystem.util.icon.Profile
import com.turnin.core.designsystem.util.icon.Thunder
import com.turnin.core.designsystem.util.icon.TurninIcons
import com.turnin.core.presentation.common.navigation.args.UserProfileArgs
import com.turnin.presentation.R
import com.turnin.presentation.home.view.HomeScreen
import com.turnin.presentation.home.viewmodel.HomeViewModel

@Composable
fun HomeRoute(
    onNavigateToKeywordDetail: (userId: Long, userKeywordId: Long) -> Unit,
    onNavigateToUserProfile: (UserProfileArgs) -> Unit,
    onNavigateToNotification: () -> Unit,
    onNavigateToDiscover: () -> Unit,
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val allFeeds = viewModel.allFeedsPagingData.collectAsLazyPagingItems()
    val friendFeeds = viewModel.friendsPagingData.collectAsLazyPagingItems()

    val allFeedsListState = allFeeds.rememberLazyListState()
    val friendFeedsListState = friendFeeds.rememberLazyListState()

    val allFeedsDropDownMenuItemValue = stringResource(R.string.home_screen_drop_down_menu_item_all)
    val friendFeedsDropDownMenuItemValue = stringResource(R.string.home_screen_drop_down_menu_item_friend)
    val dropDownMenuState = rememberDropDownMenuState(
        items = listOf(
            TurninDropDownMenuItem(
                value = allFeedsDropDownMenuItemValue,
                icon = TurninIcons.Outlined.Normal.Thunder,
            ),
            TurninDropDownMenuItem(
                value = friendFeedsDropDownMenuItemValue,
                icon = TurninIcons.Outlined.Normal.Profile,
            ),
        ),
    )

    val (feeds, lazyListState) = when (dropDownMenuState.selectedIndex) {
        0 -> allFeeds to allFeedsListState
        else -> friendFeeds to friendFeedsListState
    }

    LaunchedEffect(dropDownMenuState.selectedIndex) {
        if (dropDownMenuState.selectedIndex == 1) {
            viewModel.initialLoadFriendsPagingData()
        }
    }

    HomeScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(TurninTheme.colorScheme.backgroundNormal),
        feeds = feeds,
        dropDownMenuState = dropDownMenuState,
        lazyListState = lazyListState,
        onFeedClick = { feed ->
            onNavigateToKeywordDetail(feed.userId, feed.userKeywordId)
        },
        onUserClick = { args ->
            onNavigateToUserProfile(args)
        },
        onNotificationClick = {
            onNavigateToNotification()
        },
        onNavigateToDiscover = {
            onNavigateToDiscover()
        },
    )
}

@Composable
private fun <T : Any> LazyPagingItems<T>.rememberLazyListState(): LazyListState = when (itemCount) {
    0 -> remember(this) { LazyListState(0, 0) }
    else -> androidx.compose.foundation.lazy.rememberLazyListState()
}
